package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import de.pcfreak9000.spaceawaits.world.World;

public class PhysicsForcesSystem extends IteratingSystem {
    
    private final ComponentMapper<PhysicsComponent> physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
    
    private final World world;
    
    public PhysicsForcesSystem(World world) {
        super(Family.all(PhysicsComponent.class).get());
        this.world = world;
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent comp = physicsMapper.get(entity);
        if (comp.body.getBody().getType() == BodyType.StaticBody) {
            return;
        }
        comp.body.applyAccelerationPh(0, -9.81f);
        
        //Problem 1: welche Fixture/Shape/Rectangle für buoyancy nehmen? Bedenke mehrere Fixtures haben nebeneffekte von zu viel drag etc...
        //Problem 2: Was ist mit Kreisen?
    }
}
