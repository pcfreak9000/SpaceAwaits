package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;

public class HealthComponent implements NBTSerializable, Component {
    
    static {
        GameRegistry.WORLD_COMPONENT_REGISTRY.register("HealthComponent", HealthComponent.class);
    }
    
    public float maxHealth;
    public float currentHealth;
    
    @Override
    public void readNBT(NBTTag tag) {
        NBTCompound c = (NBTCompound) tag;
        currentHealth = c.getFloat("healthCurrent");
        //maxHealth = c.getFloat("healthMax");
    }
    
    @Override
    public NBTTag writeNBT() {
        NBTCompound c = new NBTCompound();
        c.putFloat("healthCurrent", currentHealth);
        //c.putFloat("healthMax", maxHealth);
        return c;
    }
    
}
