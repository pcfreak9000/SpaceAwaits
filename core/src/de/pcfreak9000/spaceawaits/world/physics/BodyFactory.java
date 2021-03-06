package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public interface BodyFactory {
    
    //If this class is reused for space as well this isn't suitable anymore
    public static final UnitConversion METER_CONV = PhysicsSystemBox2D.METER_CONV;
    
    Body createBody(World world);
    
    default void destroyBody(Body body, World world) {
        world.destroyBody(body);
    }
    
    Vector2 bodyOffset();
    
    Vector2 boundingBoxWidthAndHeight();
}
