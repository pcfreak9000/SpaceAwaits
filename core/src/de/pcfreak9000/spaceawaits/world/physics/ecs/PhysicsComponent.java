package de.pcfreak9000.spaceawaits.world.physics.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.box2d.structs.b2ShapeId;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.serialize.NBTSerialize;

@NBTSerialize(key = "spaceawaitsPhysics")
public class PhysicsComponent implements Component {

    public boolean considerSensorsAsBlocking = false;
    public BodyWrapper body;
    public IBodyFactory factory;

    public boolean affectedByForces = true;
    //do not touch
    public Array<b2ShapeId> i_nonsensorfixtures;
    boolean i_tmpadded = false;

    @NBTSerialize(key = "vx")
    float xVel;

    @NBTSerialize(key = "vy")
    float yVel;

    @NBTSerialize(key = "vr")
    float rotVel;

    // TransformComponent problem and UnitConversion problem

}
