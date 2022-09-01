package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.Component;

import de.omnikryptec.util.Logger;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;
import de.pcfreak9000.spaceawaits.serialize.NBTSerialize;

@NBTSerialize(key = "spaceawaitsItemstackComp")
public class ItemStackComponent implements Component, INBTSerializable {
    
    public ItemStack stack;
    
    @Override
    public void readNBT(NBTCompound tag) {
        this.stack = ItemStack.readNBT(tag);
    }
    
    @Override
    public void writeNBT(NBTCompound nbt) {
        if (stack == null) {
            Logger.getLogger(getClass()).warn("Null itemstack detected, writing empty itemstack");
            ItemStack.writeNBT(ItemStack.EMPTY, nbt);
        }
        ItemStack.writeNBT(stack, nbt);
    }
}
