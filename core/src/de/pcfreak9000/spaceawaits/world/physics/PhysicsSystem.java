package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import de.omnikryptec.math.Mathf;
import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceawaits.world.IChunkProvider;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk.ChunkGenStage;
import de.pcfreak9000.spaceawaits.world.ecs.ModifiedEngine;
import de.pcfreak9000.spaceawaits.world.ecs.SystemCache;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class PhysicsSystem extends IteratingSystem implements EntityListener {
    
    private static final Logger LOGGER = Logger.getLogger(PhysicsSystem.class);
    
    private static final float STEPLENGTH_SECONDS = de.pcfreak9000.spaceawaits.world.World.STEPLENGTH_SECONDS;
    private static final float PIXELS_PER_METER = 1f;
    
    private static final float QUERYXY_OFFSET = 0.01f;
    
    //Consider subclassing World and putting the unitconversion there. Might be useful when space arrives
    public static final UnitConversion METER_CONV = new UnitConversion(PIXELS_PER_METER);
    
    public static void syncTransformToBody(Entity entity) {
        if (Components.TRANSFORM.has(entity) && Components.PHYSICS.has(entity)) {
            TransformComponent tc = Components.TRANSFORM.get(entity);
            PhysicsComponent pc = Components.PHYSICS.get(entity);
            Vector2 pos = pc.body.getPositionW();
            tc.position.set(pos.x - pc.factory.bodyOffset().x, pos.y - pc.factory.bodyOffset().y);
            tc.originx = pc.factory.bodyOffset().x;
            tc.originy = pc.factory.bodyOffset().y;
            tc.rotation = pc.body.getRotation();
            Vector2 vel = pc.body.getLinearVelocityPh();
            pc.xVel = vel.x;
            pc.yVel = vel.y;
            pc.rotVel = pc.body.getBody().getAngularVelocity();
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
    
    private World box2dWorld;
    
    private ContactListenerImpl contactEventDispatcher;
    
    private final RaycastCallbackBox2DImpl raycastImpl = new RaycastCallbackBox2DImpl();
    private final RaycastCallbackEntityImpl raycastCallbackWr = new RaycastCallbackEntityImpl();
    private final QueryCallbackBox2DImpl queryImpl = new QueryCallbackBox2DImpl();
    private final EntityOccupationChecker entCheck = new EntityOccupationChecker();
    
    private final Array<Entity> tmpEntities = new Array<>(false, 10);
    private int tmpEntitiesDepth = 0;
    
    private final UserDataHelper udh = new UserDataHelper();
    
    private final SystemCache<TileSystem> tiles = new SystemCache<>(TileSystem.class);
    
    private final IChunkProvider chunkProvider;
    private final de.pcfreak9000.spaceawaits.world.World world;
    
    public PhysicsSystem(de.pcfreak9000.spaceawaits.world.World world, IChunkProvider chProv) {
        super(Family.all(PhysicsComponent.class).get());
        this.chunkProvider = chProv;
        this.world = world;
        this.box2dWorld = new World(new Vector2(0, 0), true);
        this.box2dWorld.setAutoClearForces(true);
        this.contactEventDispatcher = new ContactListenerImpl(world, METER_CONV);
        this.box2dWorld.setContactListener(contactEventDispatcher);
    }
    
    //for the PhysicsDebugRendererSystem
    public World getB2DWorld() {
        return box2dWorld;
    }
    
    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(getFamily(), this);
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
        this.box2dWorld.step(STEPLENGTH_SECONDS, 5, 2);
        //this.box2dWorld.clearForces();
        for (Entity e : getEntities()) {
            post(e);
        }
    }
    
    public void raycast(float x1, float y1, float x2, float y2, IRaycastFixtureCallback callback) {
        ensureChunksAABB(x1, y1, x2, y2);
        x1 = METER_CONV.in(x1);
        y1 = METER_CONV.in(y1);
        x2 = METER_CONV.in(x2);
        y2 = METER_CONV.in(y2);
        raycastImpl.callback = callback;
        this.box2dWorld.rayCast(raycastImpl, x1, y1, x2, y2);
        raycastImpl.callback = null;
        unensureChunks();
    }
    
    public void raycastEntities(float x1, float y1, float x2, float y2, IRaycastEntityCallback callback) {
        raycastCallbackWr.callb = callback;//FIXME? Might trigger multiple times for one entity with multiple fixtures
        raycast(x1, y1, x2, y2, raycastCallbackWr);
        raycastCallbackWr.callb = null;
    }
    
    public boolean checkRectOccupation(float x, float y, float w, float h, boolean canSensorsBlock) {
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
        this.box2dWorld.QueryAABB(queryImpl, x1, y1, x2, y2);
        queryImpl.callback = null;
        unensureChunks();
    }
    
    public void queryXYc(float x, float y, IQueryCallback callback) {
        queryAABB(x - QUERYXY_OFFSET, y - QUERYXY_OFFSET, x + QUERYXY_OFFSET, y + QUERYXY_OFFSET, (fix, uc) -> {
            if (fix.testPoint(uc.in(x), uc.in(y))) {
                return callback.reportFixture(fix, uc);
            }
            return true;
        });
    }
    
    public Array<Object> queryXY(float x, float y, IQueryFilter filter) {
        Array<Object> results = new Array<>();
        queryXYc(x, y, (fix, uc) -> {
            udh.set(fix.getUserData(), fix);
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
        if (pc.tmpadded && entity.flags == ModifiedEngine.FLAG_ADDED) {
            tmpEntities.removeValue(entity, true);
            pc.tmpadded = false;
        }
        if (pc.body != null) {
            LOGGER.error("Body of just added entity isn't null: " + entity);
            Thread.dumpStack();
        }
        pc.body = new BodyWrapper(pc.factory.createBody(box2dWorld, entity));
        pc.body.getBody().setLinearVelocity(pc.xVel, pc.yVel);
        pc.body.getBody().setAngularVelocity(pc.rotVel);
        if (pc.body.getBody().getUserData() == null) {
            pc.body.getBody().setUserData(entity);
            for (Fixture f : pc.body.getBody().getFixtureList()) {//Hmmm... have this loop in that if or put it outside?
                if (f.getUserData() == null) {
                    f.setUserData(entity);
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
    
    private void unensureChunks() {
        if (tmpEntitiesDepth <= 0) {
            throw new IllegalStateException("Too much unensuring");
        }
        tmpEntitiesDepth--;
        if (tmpEntitiesDepth == 0) {
            while (tmpEntities.size > 0) {
                Entity e = tmpEntities.pop();
                if (e.flags != ModifiedEngine.FLAG_ADDED) {
                    entityRemoved(e);
                }
            }
        }
    }
    
    private void ensureChunksAABB(float x1, float y1, float x2, float y2) {
        if (chunkProvider == null) {
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
        //would be nice if coordinate point of things always was the lower left corner, then the upper bounds +1 could be left out
        for (int i = cx1 - 1; i <= cx2 + 1; i++) {
            for (int j = cy1 - 1; j <= cy2 + 1; j++) {
                if (world.getBounds().inBoundsChunk(i, j)) {
                    Chunk c = chunkProvider.getChunk(i, j);
                    if (c == null) { //hmm                        
                        continue;
                    }
                    if (!c.isActive() && c.getGenStage().level >= ChunkGenStage.Populated.level) {
                        for (Entity e : c.getEntities()) {
                            if (getFamily().matches(e)) {
                                if (e.flags != ModifiedEngine.FLAG_ADDED && !Components.PHYSICS.get(e).tmpadded) {
                                    Components.PHYSICS.get(e).tmpadded = true;
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
    }
    
    private static final class QueryCallbackBox2DImpl implements QueryCallback {
        
        private IQueryCallback callback;
        
        @Override
        public boolean reportFixture(Fixture fixture) {
            return callback.reportFixture(fixture, METER_CONV);
        }
        
    }
    
    private static final class RaycastCallbackBox2DImpl implements RayCastCallback {
        
        private IRaycastFixtureCallback callback;
        
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            return callback.reportRayFixture(fixture, METER_CONV.out(point.x), METER_CONV.out(point.y), normal.x,
                    normal.y, fraction, METER_CONV);
        }
        
    }
    
    private static final class RaycastCallbackEntityImpl implements IRaycastFixtureCallback {
        private IRaycastEntityCallback callb;
        private final UserDataHelper ud = new UserDataHelper();
        
        @Override
        public float reportRayFixture(Fixture fixture, float pointx, float pointy, float normalx, float normaly,
                float fraction, UnitConversion conv) {
            //ignore everything which is not an Entity
            ud.set(fixture.getUserData(), fixture);
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
        public boolean reportFixture(Fixture fix, UnitConversion conv) {
            ud.set(fix.getUserData(), fix);
            if (canSensorsBlock && ud.isEntity() && fix.isSensor()) {
                blocking = Components.PHYSICS.get(ud.getEntity()).considerSensorsAsBlocking;
            } else {
                blocking = ud.isEntity();
            }
            return !blocking;
        }
        
    }
}
