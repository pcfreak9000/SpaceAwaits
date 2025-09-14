package de.pcfreak9000.spaceawaits.world.physics.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.box2d.Box2d.b2CastResultFcn;
import com.badlogic.gdx.box2d.Box2d.b2OverlapResultFcn;
import com.badlogic.gdx.box2d.structs.b2AABB;
import com.badlogic.gdx.box2d.structs.b2ContactBeginTouchEvent;
import com.badlogic.gdx.box2d.structs.b2ContactEndTouchEvent;
import com.badlogic.gdx.box2d.structs.b2ContactEvents;
import com.badlogic.gdx.box2d.structs.b2Manifold;
import com.badlogic.gdx.box2d.structs.b2QueryFilter;
import com.badlogic.gdx.box2d.structs.b2SensorBeginTouchEvent;
import com.badlogic.gdx.box2d.structs.b2SensorEndTouchEvent;
import com.badlogic.gdx.box2d.structs.b2SensorEvents;
import com.badlogic.gdx.box2d.structs.b2ShapeId;
import com.badlogic.gdx.box2d.structs.b2ShapeId.b2ShapeIdPointer;
import com.badlogic.gdx.box2d.structs.b2Vec2;
import com.badlogic.gdx.box2d.structs.b2WorldDef;
import com.badlogic.gdx.box2d.structs.b2WorldId;
import com.badlogic.gdx.jnigen.runtime.closure.ClosureObject;
import com.badlogic.gdx.jnigen.runtime.pointer.VoidPointer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.omnikryptec.math.Mathf;
import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceawaits.core.ecs.EngineImproved;
import de.pcfreak9000.spaceawaits.core.ecs.SystemCache;
import de.pcfreak9000.spaceawaits.core.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk.ChunkGenStage;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkSystem;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.physics.IDFactory;
import de.pcfreak9000.spaceawaits.world.physics.IQueryCallback;
import de.pcfreak9000.spaceawaits.world.physics.IQueryFilter;
import de.pcfreak9000.spaceawaits.world.physics.IRaycastEntityCallback;
import de.pcfreak9000.spaceawaits.world.physics.IRaycastFixtureCallback;
import de.pcfreak9000.spaceawaits.world.physics.UnitConversion;
import de.pcfreak9000.spaceawaits.world.physics.UserDataHelper;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class PhysicsSystem extends IteratingSystem implements EntityListener {

	private static final Logger LOGGER = Logger.getLogger(PhysicsSystem.class);

	private static final float STEPLENGTH_SECONDS = GameScreen.STEPLENGTH_SECONDS;
	private static final float PIXELS_PER_METER = 1f;

	private static final float QUERYXY_OFFSET = 0.01f;

	// Consider subclassing World and putting the unitconversion there. Might be
	// useful when space arrives
	public static final UnitConversion METER_CONV = new UnitConversion(PIXELS_PER_METER);

	public static void syncTransformToBody(Entity entity) {
		if (Components.TRANSFORM.has(entity) && Components.PHYSICS.has(entity)) {
			TransformComponent tc = Components.TRANSFORM.get(entity);
			PhysicsComponent pc = Components.PHYSICS.get(entity);
			Vector2 pos = pc.body.getPositionW();
			tc.position.set(pos.x - pc.factory.bodyOffset().x, pos.y - pc.factory.bodyOffset().y);
			tc.rotoffx = pc.factory.bodyOffset().x;
			tc.rotoffy = pc.factory.bodyOffset().y;
			tc.rotation = pc.body.getRotation();
			Vector2 vel = pc.body.getLinearVelocityPh();
			pc.xVel = vel.x;
			pc.yVel = vel.y;
			pc.rotVel = Box2d.b2Body_GetAngularVelocity(pc.body.getBody());
		}
	}

	public static void syncBodyToTransform(Entity entity) {
		if (Components.TRANSFORM.has(entity) && Components.PHYSICS.has(entity)) {
			PhysicsComponent pc = Components.PHYSICS.get(entity);
			TransformComponent tc = Components.TRANSFORM.get(entity);
			pc.body.setTransformW(tc.position.x + pc.factory.bodyOffset().x, tc.position.y + pc.factory.bodyOffset().y,
					tc.rotation);
		}
	}

	private b2WorldId box2dWorld;

	private ContactListenerImpl contactEventDispatcher;

	private final b2QueryFilter queryfilter = Box2d.b2DefaultQueryFilter();
	private final RaycastCallbackBox2DImpl raycastImpl = new RaycastCallbackBox2DImpl();
	private final ClosureObject<b2CastResultFcn> clRaycastImpl = ClosureObject.fromClosure(raycastImpl);
	private final RaycastCallbackEntityImpl raycastCallbackWr = new RaycastCallbackEntityImpl();
	private final QueryCallbackBox2DImpl queryImpl = new QueryCallbackBox2DImpl();
	private final ClosureObject<b2OverlapResultFcn> clQueryImpl = ClosureObject.fromClosure(queryImpl);
	private final EntityOccupationChecker entCheck = new EntityOccupationChecker();

	private final Array<Entity> tmpEntities = new Array<>(false, 10);
	private int tmpEntitiesDepth = 0;

	private final UserDataHelper udh = new UserDataHelper();

	// eh...
	private final SystemCache<TileSystem> tiles = new SystemCache<>(TileSystem.class);
	private final SystemCache<ChunkSystem> csys = new SystemCache<>(ChunkSystem.class);

	public PhysicsSystem() {
		super(Family.all(PhysicsComponent.class).get());
		b2WorldDef worldDef = Box2d.b2DefaultWorldDef();
		b2Vec2 gravity = worldDef.gravity();
		gravity.x(0.0f);
		gravity.y(0.0f);
		this.box2dWorld = Box2d.b2CreateWorld(worldDef.asPointer());
		// Box2d.b2DefaultDebugDraw().Fcn
		// this.box2dWorld.setAutoClearForces(true);
		this.contactEventDispatcher = new ContactListenerImpl(METER_CONV);
//		this.box2dWorld.setContactListener(contactEventDispatcher);
	}

	// for the PhysicsDebugRendererSystem
	public b2WorldId getB2DWorld() {
		return box2dWorld;
	}

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(getFamily(), this);
		contactEventDispatcher.setEngine(engine);
	}

	@Override
	public void removedFromEngine(Engine engine) {
		super.removedFromEngine(engine);
		engine.removeEntityListener(this);
	}

	@Override
	public void update(float deltat) {
		for (Entity e : getEntities()) {
			processEntity(e, deltat);
		}
		// this.box2dWorld.step(STEPLENGTH_SECONDS, 5, 2);
		Box2d.b2World_Step(box2dWorld, STEPLENGTH_SECONDS, 5);
		// TODO handle contact events
		b2ContactEvents contactevs = Box2d.b2World_GetContactEvents(box2dWorld);
		int begincount = contactevs.beginCount();
		for (int i = 0; i < begincount; i++) {
			b2ContactBeginTouchEvent beg = contactevs.beginEvents().get(i);
			b2ShapeId shapea = beg.getShapeIdA();
			b2ShapeId shapeb = beg.getShapeIdB();
			b2Manifold manifold = beg.getManifold();
			this.contactEventDispatcher.beginContact(shapea, shapeb, manifold);
		}
		int endcount = contactevs.endCount();
		for (int i = 0; i < endcount; i++) {
			b2ContactEndTouchEvent beg = contactevs.endEvents().get(i);
			b2ShapeId shapea = beg.getShapeIdA();
			b2ShapeId shapeb = beg.getShapeIdB();
			this.contactEventDispatcher.endContact(shapea, shapeb);
		}
		b2SensorEvents sensevs = Box2d.b2World_GetSensorEvents(box2dWorld);
		int begincountsens = sensevs.beginCount();
		for (int i = 0; i < begincountsens; i++) {
			b2SensorBeginTouchEvent beg = sensevs.beginEvents().get(i);
			b2ShapeId shapea = beg.getSensorShapeId();
			b2ShapeId shapeb = beg.getVisitorShapeId();
			this.contactEventDispatcher.beginContact(shapea, shapeb, null);
		}
		int endcountsens = sensevs.endCount();
		for (int i = 0; i < endcountsens; i++) {
			b2SensorEndTouchEvent beg = sensevs.endEvents().get(i);
			b2ShapeId shapea = beg.getSensorShapeId();
			b2ShapeId shapeb = beg.getVisitorShapeId();
			this.contactEventDispatcher.endContact(shapea, shapeb);
		}
		// this.box2dWorld.clearForces();
		for (Entity e : getEntities()) {
			post(e);
		}
	}

	public void raycast(float x1, float y1, float x2, float y2, IRaycastFixtureCallback callback) {
		// TODO needs cleanup
		ensureChunksAABB(x1, y1, x2, y2);
		x1 = METER_CONV.in(x1);
		y1 = METER_CONV.in(y1);
		x2 = METER_CONV.in(x2);
		y2 = METER_CONV.in(y2);
		raycastImpl.callback = callback;
		b2Vec2 origin = new b2Vec2();
		b2Vec2 translation = new b2Vec2();
		origin.x(x1);
		origin.y(y1);
		translation.x(x2 - x1);
		translation.y(y2 - y1);
		// this.box2dWorld.rayCast(raycastImpl, x1, y1, x2, y2);
		Box2d.b2World_CastRay(box2dWorld, origin, translation, queryfilter, clRaycastImpl, null);
		raycastImpl.callback = null;
		unensureChunks();
	}

	public void raycastEntities(float x1, float y1, float x2, float y2, IRaycastEntityCallback callback) {
		raycastCallbackWr.callb = callback;// FIXME? Might trigger multiple times for one entity with multiple fixtures
		raycast(x1, y1, x2, y2, raycastCallbackWr);
		raycastCallbackWr.callb = null;
	}

	public boolean checkRectOccupation(float x, float y, float w, float h, boolean canSensorsBlock) {
		if (tiles.get(getEngine()) != null) {
			int ix = Mathf.floori(x);
			int iy = Mathf.floori(y);
			int iw = Mathf.ceili(x + w);
			int ih = Mathf.ceili(y + h);
			for (int i = ix; i < iw; i++) {
				for (int j = iy; j < ih; j++) {
					Tile t = tiles.get(getEngine()).getTile(i, j, TileLayer.Front);
					if (t.isSolid()) {
						return true;
					}
				}
			}
		}
		if (checkRectEntityOccupation(x, y, x + w, y + h, canSensorsBlock)) {
			return true;
		}
		return false;
	}

	public boolean checkRectEntityOccupation(float x1, float y1, float x2, float y2, boolean canSensorsBlock) {
		entCheck.ud.clear();
		entCheck.canSensorsBlock = canSensorsBlock;
		queryAABB(x1, y1, x2, y2, entCheck);
		boolean b = entCheck.blocking;
		return b;
	}

	public void queryAABB(float x1, float y1, float x2, float y2, IQueryCallback callback) {
		ensureChunksAABB(x1, y1, x2, y2);
		x1 = METER_CONV.in(x1);
		y1 = METER_CONV.in(y1);
		x2 = METER_CONV.in(x2);
		y2 = METER_CONV.in(y2);
		queryImpl.callback = callback;
		// TODO needs cleanup
		b2AABB aabb = new b2AABB();
		aabb.lowerBound().x(Math.min(x1, x2));
		aabb.lowerBound().y(Math.min(y1, y2));
		aabb.upperBound().x(Math.max(x1, x2));
		aabb.upperBound().y(Math.max(y1, y2));
		Box2d.b2World_OverlapAABB(box2dWorld, aabb, queryfilter, clQueryImpl, VoidPointer.NULL);
		// this.box2dWorld.QueryAABB(queryImpl, x1, y1, x2, y2);
		queryImpl.callback = null;
		unensureChunks();
	}

	public void queryXYc(float x, float y, IQueryCallback callback) {
		queryAABB(x - QUERYXY_OFFSET, y - QUERYXY_OFFSET, x + QUERYXY_OFFSET, y + QUERYXY_OFFSET, (fix, uc) -> {
			b2Vec2 point = new b2Vec2();
			point.x(x);// TODO needs cleanup
			point.y(y);
			if (Box2d.b2Shape_TestPoint(fix, point)) {
				return callback.reportFixture(fix, uc);
			}
			return true;
		});
	}

	public Array<Object> queryXY(float x, float y, IQueryFilter filter) {
		Array<Object> results = new Array<>();
		queryXYc(x, y, (fix, uc) -> {
			udh.set(Box2d.b2Shape_GetUserData(fix), fix);
			if (filter.accept(udh, uc)) {
				results.add(udh.getUserDataRaw());
			}
			return true;
		});
		return results;
	}

	private void post(Entity entity) {
		syncTransformToBody(entity);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		syncBodyToTransform(entity);
	}

	@Override
	public void entityAdded(Entity entity) {
		PhysicsComponent pc = Components.PHYSICS.get(entity);
		if (pc.i_tmpadded && entity.flags == EngineImproved.FLAG_ADDED) {
			tmpEntities.removeValue(entity, true);
			pc.i_tmpadded = false;
		}
		if (pc.body != null) {
			LOGGER.error("Body of just added entity isn't null: " + entity);
			Thread.dumpStack();
		}
		pc.body = new BodyWrapper(pc.factory.createBody(box2dWorld, entity));
		b2Vec2 helper = new b2Vec2();
		helper.x(pc.xVel);
		helper.y(pc.yVel);
		Box2d.b2Body_SetLinearVelocity(pc.body.getBody(), helper);
		// pc.body.getBody().setLinearVelocity(pc.xVel, pc.yVel);
		Box2d.b2Body_SetAngularVelocity(pc.body.getBody(), pc.rotVel);
		// pc.body.getBody().setAngularVelocity(pc.rotVel);
		VoidPointer vp = Box2d.b2Body_GetUserData(pc.body.getBody());
		if (vp == VoidPointer.NULL) {
			vp = IDFactory.putData(entity);
			Box2d.b2Body_SetUserData(pc.body.getBody(), vp);
			int count = Box2d.b2Body_GetShapeCount(pc.body.getBody());
			b2ShapeIdPointer p = new b2ShapeIdPointer(count, true);
			Box2d.b2Body_GetShapes(pc.body.getBody(), p, count);
			for (int i = 0; i < count; i++) {
				b2ShapeId shape = p.get(i);
				if (Box2d.b2Shape_GetUserData(shape) == VoidPointer.NULL) {
					Box2d.b2Shape_SetUserData(shape, vp);
				}
			}
		}
	}

	@Override
	public void entityRemoved(Entity entity) {
		PhysicsComponent pc = Components.PHYSICS.get(entity);
		pc.factory.destroyBody(pc.body.getBody(), box2dWorld);
		pc.body = null;
	}

	// TODO this stuff belongs somewhere else, this is not physics related, or is
	// it?

	private void unensureChunks() {
		if (csys.get(getEngine()) == null) {
			return;
		}
		if (tmpEntitiesDepth <= 0) {
			throw new IllegalStateException("Too much unensuring");
		}
		tmpEntitiesDepth--;
		if (tmpEntitiesDepth == 0) {
			while (tmpEntities.size > 0) {
				Entity e = tmpEntities.pop();
				if (e.flags != EngineImproved.FLAG_ADDED) {
					entityRemoved(e);
				}
			}
		}
	}

	private void ensureChunksAABB(float x1, float y1, float x2, float y2) {
		if (csys.get(getEngine()) == null) {
			return;
		}
		tmpEntitiesDepth++;
		int cx1 = Chunk.toGlobalChunkf(x1);
		int cy1 = Chunk.toGlobalChunkf(y1);
		int cx2 = Chunk.toGlobalChunkf(x2);
		int cy2 = Chunk.toGlobalChunkf(y2);
		if (cx1 > cx2) {
			int t = cx2;
			cx2 = cx1;
			cx1 = t;
		}
		if (cy1 > cy2) {
			int t = cy2;
			cy2 = cy1;
			cy1 = t;
		}
		// would be nice if coordinate point of things always was the lower left corner,
		// then the upper bounds +1 could be left out
		for (int i = cx1 - 1; i <= cx2 + 1; i++) {
			for (int j = cy1 - 1; j <= cy2 + 1; j++) {
				Chunk c = csys.get(getEngine()).getChunk(i, j);
				if (c == null) { // hmm
					continue;
				}
				if (!c.isActive() && c.getGenStage().level >= ChunkGenStage.Populated.level) {
					for (Entity e : c.getEntities()) {
						if (getFamily().matches(e)) {
							if (e.flags != EngineImproved.FLAG_ADDED && !Components.PHYSICS.get(e).i_tmpadded) {
								Components.PHYSICS.get(e).i_tmpadded = true;
								tmpEntities.add(e);
								entityAdded(e);
								syncBodyToTransform(e);
							}
						}
					}
				}
			}
		}
	}

	private static final class QueryCallbackBox2DImpl implements b2OverlapResultFcn {

		private IQueryCallback callback;

		@Override
		public boolean b2OverlapResultFcn_call(b2ShapeId shapeId, VoidPointer context) {
			return callback.reportFixture(shapeId, METER_CONV);
		}

//        @Override
//        public boolean reportFixture(Fixture fixture) {
//            return callback.reportFixture(fixture, METER_CONV);
//        }

	}

	private static final class RaycastCallbackBox2DImpl implements b2CastResultFcn {

		private IRaycastFixtureCallback callback;

//        @Override
//        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
//            return callback.reportRayFixture(fixture, METER_CONV.out(point.x), METER_CONV.out(point.y), normal.x,
//                    normal.y, fraction, METER_CONV);
//        }

		@Override
		public float b2CastResultFcn_call(b2ShapeId shapeId, b2Vec2 point, b2Vec2 normal, float fraction,
				VoidPointer context) {
			return callback.reportRayFixture(shapeId, point.x(), point.y(), normal.x(), normal.y(), fraction, null);
		}

	}

	private static final class RaycastCallbackEntityImpl implements IRaycastFixtureCallback {
		private IRaycastEntityCallback callb;
		private final UserDataHelper ud = new UserDataHelper();

		@Override
		public float reportRayFixture(b2ShapeId fixture, float pointx, float pointy, float normalx, float normaly,
				float fraction, UnitConversion conv) {
			// ignore everything which is not an Entity
			ud.set(Box2d.b2Shape_GetUserData(fixture), fixture);
			if (!ud.isEntity()) {
				return -1;
			}
			return callb.reportRayEntity(ud.getEntity(), pointx, pointy, normalx, normaly, fraction, conv);
		}
	}

	private static final class EntityOccupationChecker implements IQueryCallback {

		public final UserDataHelper ud = new UserDataHelper();

		public boolean blocking;

		public boolean canSensorsBlock;

		@Override
		public boolean reportFixture(b2ShapeId fix, UnitConversion conv) {
			ud.set(Box2d.b2Shape_GetUserData(fix), fix);
			if (canSensorsBlock && ud.isEntity() && Box2d.b2Shape_IsSensor(fix)) {
				blocking = Components.PHYSICS.get(ud.getEntity()).considerSensorsAsBlocking;
			} else {
				blocking = ud.isEntity();
			}
			return !blocking;
		}

	}
}
