package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.ashley.core.ComponentMapper;
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

import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceawaits.world.IQueryCallback;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;

public class PhysicsSystemBox2D extends IteratingSystem implements EntityListener {
    
    private final ComponentMapper<TransformComponent> transformMapper = ComponentMapper
            .getFor(TransformComponent.class);
    private final ComponentMapper<PhysicsComponent> physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
    
    private static final float STEPSIZE_SECONDS = 1 / 60f;
    private static final float PIXELS_PER_METER = 1.5f;
    
    //Consider subclassing World and putting the unitconversion there. Might be useful when space arrives
    public static final UnitConversion METER_CONV = new UnitConversion(PIXELS_PER_METER);
    
    private float deltaAcc = 0;
    
    private World box2dWorld;
    
    private ContactListenerImpl contactEventDispatcher;
    
    public PhysicsSystemBox2D(de.pcfreak9000.spaceawaits.world.World world) {
        super(Family.all(PhysicsComponent.class).get());
        this.box2dWorld = new World(new Vector2(0, -9.81f), true);
        this.box2dWorld.setAutoClearForces(false);
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
        this.deltaAcc += deltat;
        if (deltaAcc > 10 * STEPSIZE_SECONDS) {
            Logger.getLogger(PhysicsSystemBox2D.class).warnf("Skipping physics ticks, acc. physics time: %f", deltaAcc);
            deltaAcc = 10 * STEPSIZE_SECONDS;
        }
        for (Entity e : getEntities()) {
            processEntity(e, deltat);
        }
        while (deltaAcc >= STEPSIZE_SECONDS) {
            deltaAcc -= STEPSIZE_SECONDS;
            this.box2dWorld.step(STEPSIZE_SECONDS, 5, 2);
        }
        this.box2dWorld.clearForces();
        for (Entity e : getEntities()) {
            post(e);
        }
    }
    
    private final RaycastCallbackImpl raycastImpl = new RaycastCallbackImpl();
    private final QueryCallbackImpl queryImpl = new QueryCallbackImpl();
    
    public void raycast(IRaycastFixtureCallback callback, float x1, float y1, float x2, float y2) {
        x1 = METER_CONV.in(x1);
        y1 = METER_CONV.in(y1);
        x2 = METER_CONV.in(x2);
        y2 = METER_CONV.in(y2);
        raycastImpl.callback = callback;
        this.box2dWorld.rayCast(raycastImpl, x1, y1, x2, y2);
        raycastImpl.callback = null;
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
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (transformMapper.has(entity)) {
            PhysicsComponent pc = this.physicsMapper.get(entity);
            TransformComponent tc = this.transformMapper.get(entity);
            pc.body.setTransformW(tc.position.x + pc.factory.bodyOffset().x, tc.position.y + pc.factory.bodyOffset().y,
                    0);
        }
    }
    
    private void post(Entity entity) {
        if (transformMapper.has(entity)) {
            TransformComponent tc = this.transformMapper.get(entity);
            PhysicsComponent pc = this.physicsMapper.get(entity);
            Vector2 pos = pc.body.getPositionW();
            tc.position.set(pos.x - pc.factory.bodyOffset().x, pos.y - pc.factory.bodyOffset().y);
            Vector2 vel = pc.body.getLinearVelocityPh();
            pc.xVel = vel.x;
            pc.yVel = vel.y;
            pc.rotVel = pc.body.getBody().getAngularVelocity();
        }
    }
    
    @Override
    public void entityAdded(Entity entity) {
        PhysicsComponent pc = this.physicsMapper.get(entity);
        pc.body = new BodyWrapper(pc.factory.createBody(box2dWorld));
        pc.body.getBody().setLinearVelocity(pc.xVel, pc.yVel);
        pc.body.getBody().setAngularVelocity(pc.rotVel);
        if (entity.flags != 1) {//TODO this needs a better solution. Chunk fixtures are tiles and they need custom user data, not the entity.
            for (Fixture f : pc.body.getBody().getFixtureList()) {
                f.setUserData(entity);
            }
        }
    }
    
    @Override
    public void entityRemoved(Entity entity) {
        PhysicsComponent pc = this.physicsMapper.get(entity);
        pc.factory.destroyBody(pc.body.getBody(), box2dWorld);
        pc.body = null;
    }
    
    private static final class QueryCallbackImpl implements QueryCallback {
        
        private IQueryCallback callback;
        
        @Override
        public boolean reportFixture(Fixture fixture) {
            return callback.reportFixture(fixture, METER_CONV);
        }
        
    }
    
    private static final class RaycastCallbackImpl implements RayCastCallback {
        
        private IRaycastFixtureCallback callback;
        
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            return callback.reportRayFixture(fixture, METER_CONV.out(point.x), METER_CONV.out(point.y), normal.x,
                    normal.y, fraction, METER_CONV);
        }
        
    }
    
}
