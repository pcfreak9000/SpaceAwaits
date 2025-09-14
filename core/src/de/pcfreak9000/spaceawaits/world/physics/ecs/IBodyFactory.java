package de.pcfreak9000.spaceawaits.world.physics.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.box2d.structs.b2BodyId;
import com.badlogic.gdx.box2d.structs.b2WorldId;
import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.spaceawaits.world.physics.UnitConversion;

public interface IBodyFactory {
    
    //If this class is reused for space as well this isn't suitable anymore
    public static final UnitConversion METER_CONV = PhysicsSystem.METER_CONV;
    
    b2BodyId createBody(b2WorldId world, Entity entity);
    
    default void destroyBody(b2BodyId body, b2WorldId world) {
        Box2d.b2DestroyBody(body);
    }
    
    Vector2 bodyOffset();
    
    Vector2 boundingBoxWidthAndHeight();
}
