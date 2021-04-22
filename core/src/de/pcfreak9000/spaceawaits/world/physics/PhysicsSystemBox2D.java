package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import de.omnikryptec.util.Logger;
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
    
    private World bworld;
    
    public PhysicsSystemBox2D() {
        super(Family.all(PhysicsComponent.class).get());
        this.bworld = new World(new Vector2(0, -9.81f), true);
        this.bworld.setAutoClearForces(false);
        //TODO Userdata: info for raycasting and ContactListener
        this.bworld.setContactListener(new ContactListener() {
            
            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }
            
            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
            
            @Override
            public void endContact(Contact contact) {
                //find interested listeners
                //notify listeners
            }
            
            @Override
            public void beginContact(Contact contact) {
            }
        });
    }
    
    public World getWorld() {
        return bworld;
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
            this.bworld.step(STEPSIZE_SECONDS, 5, 2);
        }
        this.bworld.clearForces();
        for (Entity e : getEntities()) {
            post(e);
        }
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
        pc.body = new BodyWrapper(pc.factory.createBody(bworld));
        pc.body.getBody().setLinearVelocity(pc.xVel, pc.yVel);
        pc.body.getBody().setAngularVelocity(pc.rotVel);
    }
    
    @Override
    public void entityRemoved(Entity entity) {
        PhysicsComponent pc = this.physicsMapper.get(entity);
        pc.factory.destroyBody(pc.body.getBody(), bworld);
        pc.body = null;
    }
}
