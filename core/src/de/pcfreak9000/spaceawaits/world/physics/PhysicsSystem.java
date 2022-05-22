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

import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;

public class PhysicsSystem extends IteratingSystem implements EntityListener {
    
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
                    0);
        }
    }
    
    private World box2dWorld;
    
    private ContactListenerImpl contactEventDispatcher;
    
    private final RaycastCallbackBox2DImpl raycastImpl = new RaycastCallbackBox2DImpl();
    private final RaycastCallbackEntityImpl raycastCallbackWr = new RaycastCallbackEntityImpl();
    private final QueryCallbackBox2DImpl queryImpl = new QueryCallbackBox2DImpl();
    private final EntityOccupationChecker entCheck = new EntityOccupationChecker();
    
    private final UserDataHelper udh = new UserDataHelper();
    
    public PhysicsSystem(de.pcfreak9000.spaceawaits.world.World world) {
        super(Family.all(PhysicsComponent.class).get());
        this.box2dWorld = new World(new Vector2(0, 0), true);
        this.box2dWorld.setAutoClearForces(true);
        this.contactEventDispatcher = new ContactListenerImpl(world, METER_CONV);
        this.box2dWorld.setContactListener(contactEventDispatcher);
    }
    
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
    
    public void raycast(IRaycastFixtureCallback callback, float x1, float y1, float x2, float y2) {
        x1 = METER_CONV.in(x1);
        y1 = METER_CONV.in(y1);
        x2 = METER_CONV.in(x2);
        y2 = METER_CONV.in(y2);
        raycastImpl.callback = callback;
        this.box2dWorld.rayCast(raycastImpl, x1, y1, x2, y2);
        raycastImpl.callback = null;
    }
    
    public void raycastEntities(IRaycastEntityCallback callback, float x1, float y1, float x2, float y2) {
        raycastCallbackWr.callb = callback;
        raycast(raycastCallbackWr, x1, y1, x2, y2);
        raycastCallbackWr.callb = null;
    }
    
    public boolean checkRectEntityOccupation(float x1, float y1, float x2, float y2) {
        entCheck.ud.clear();
        queryAABB(entCheck, x1, y1, x2, y2);
        boolean b = entCheck.ud.isEntity();
        return b;
    }
    
    public void queryAABB(IQueryCallback callback, float x1, float y1, float x2, float y2) {
        x1 = METER_CONV.in(x1);
        y1 = METER_CONV.in(y1);
        x2 = METER_CONV.in(x2);
        y2 = METER_CONV.in(y2);
        queryImpl.callback = callback;
        this.box2dWorld.QueryAABB(queryImpl, x1, y1, x2, y2);
        queryImpl.callback = null;
    }
    
    public void queryXY(float x, float y, IQueryCallback callback) {
        queryAABB((fix, uc) -> {
            if (fix.testPoint(uc.in(x), uc.in(y))) {
                return callback.reportFixture(fix, uc);
            }
            return true;
        }, x - QUERYXY_OFFSET, y - QUERYXY_OFFSET, x + QUERYXY_OFFSET, y + QUERYXY_OFFSET);
    }
    
    public Array<Object> queryXY(IQueryFilter filter, float x, float y) {
        Array<Object> results = new Array<>();
        queryXY(x, y, (fix, uc) -> {
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
        pc.body = new BodyWrapper(pc.factory.createBody(box2dWorld));
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
        
        @Override
        public boolean reportFixture(Fixture fix, UnitConversion conv) {
            if (fix.isSensor()) {
                return true;
            }
            ud.set(fix.getUserData(), fix);
            return !ud.isEntity();
        }
        
    }
}
