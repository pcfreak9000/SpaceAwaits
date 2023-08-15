package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;
import de.pcfreak9000.spaceawaits.serialize.NBTSerialize;

@NBTSerialize(key = "spaceawaitsItemstack")
public class ItemStackComponent implements Component, INBTSerializable {
    
    public ItemStack stack;
    
    @Override
    public void readNBT(NBTCompound nbt) {
        stack = ItemStack.readNBT(nbt);
    }
    
    @Override
    public void writeNBT(NBTCompound nbt) {
        ItemStack.writeNBT(stack, nbt);
    }
}
