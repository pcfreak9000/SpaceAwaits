package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;

public class PhysicsComponent implements Component, NBTSerializable {
    

    public BodyWrapper body;
    public BodyFactory factory;
    
    /**
     * used for serialization
     */
    float xVel, yVel, rotVel;
    
    @Override
    public void readNBT(NBTTag compound) {
        NBTCompound c = (NBTCompound) compound;
        xVel = c.getFloatOrDefault("xv", 0);
        yVel = c.getFloatOrDefault("yv", 0);
        rotVel = c.getFloatOrDefault("rv", 0);
    }
    
    @Override
    public NBTTag writeNBT() {
        NBTCompound c = new NBTCompound();
        c.putFloat("xv", xVel);
        c.putFloat("yv", yVel);
        c.putFloat("rv", rotVel);
        return c;
    }
    
    //TransformComponent problem and UnitConversion problem
    
}
