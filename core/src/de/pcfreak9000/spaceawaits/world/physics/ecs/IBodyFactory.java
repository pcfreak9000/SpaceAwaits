package de.pcfreak9000.spaceawaits.world.physics.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import de.pcfreak9000.spaceawaits.world.physics.UnitConversion;

public interface IBodyFactory {
    
    //If this class is reused for space as well this isn't suitable anymore
    public static final UnitConversion METER_CONV = PhysicsSystem.METER_CONV;
    
    default Body createBody(World world, Entity entity) {
        return createBody(world);
    }
    
    @Deprecated
    Body createBody(World world);
    
    default void destroyBody(Body body, World world) {
        world.destroyBody(body);
    }
    
    Vector2 bodyOffset();
    
    Vector2 boundingBoxWidthAndHeight();
}
