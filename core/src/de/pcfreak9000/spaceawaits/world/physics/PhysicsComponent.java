package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.serialize.NBTSerialize;

@NBTSerialize(key = "spaceawaitsPhysics")
public class PhysicsComponent implements Component {
    
    public boolean considerSensorsAsBlocking = false;
    public BodyWrapper body;
    public BodyFactory factory;
    
    boolean tmpadded = false;
    
    @NBTSerialize(key = "vx")
    float xVel;
    
    @NBTSerialize(key = "vy")
    float yVel;
    
    @NBTSerialize(key = "vr")
    float rotVel;
    
    //TransformComponent problem and UnitConversion problem
    
}
