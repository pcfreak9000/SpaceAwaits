package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Component;

import de.omnikryptec.util.Logger;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;

public class ItemStackComponent implements Component, NBTSerializable {
    
    static {
        GameRegistry.WORLD_COMPONENT_REGISTRY.register("spaceawaitsItemStackComp", ItemStackComponent.class);
    }
    
    public ItemStack stack;
    
    @Override
    public void readNBT(NBTTag tag) {
        this.stack = ItemStack.readNBT(tag);
    }
    
    @Override
    public NBTTag writeNBT() {
        if (stack == null) {
            Logger.getLogger(getClass()).warn("Null itemstack detected, writing empty itemstack");
            return ItemStack.writeNBT(ItemStack.EMPTY);
        }
        return ItemStack.writeNBT(stack);
    }
}
