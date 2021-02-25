package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public interface BodyFactory {
    
    Body createBody(World world, UnitConversion meterconv);
    
    default void destroyBody(Body body, World world, UnitConversion meterconv) {
        world.destroyBody(body);
    }
    
    Vector2 bodyOffset();
}
