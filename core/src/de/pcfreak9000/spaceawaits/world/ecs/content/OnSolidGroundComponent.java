package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;

public class OnSolidGroundComponent implements Component, NBTSerializable {
    
    static {
        GameRegistry.WORLD_COMPONENT_REGISTRY.register("OnSolidGroundComponent", OnSolidGroundComponent.class);
    }
    
    public int solidGroundContacts;
    public float lastContactX;
    public float lastContactY;
    
    @Override
    public void readNBT(NBTTag tag) {
        NBTCompound n = (NBTCompound) tag;
        lastContactX = n.getFloat("lastContactX");
        lastContactY = n.getFloat("lastContactY");
    }
    
    @Override
    public NBTTag writeNBT() {
        NBTCompound n = new NBTCompound();
        n.putFloat("lastContactX", lastContactX);
        n.putFloat("lastContactY", lastContactY);
        return n;
    }
    
    public boolean isOnSolidGround() {
        return solidGroundContacts > 0;
    }
}
