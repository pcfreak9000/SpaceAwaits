package de.pcfreak9000.spaceawaits.content.tiles;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;
import de.pcfreak9000.spaceawaits.serialize.NBTSerialize;
import de.pcfreak9000.spaceawaits.world.tile.ITileEntity;

public class TileEntityPrimitiveFurnace implements IInventory, INBTSerializable, ITileEntity {
    
    private ItemStack[] stacks = new ItemStack[3];
    
    @NBTSerialize(key = "burntimeLeft")
    private float partialBurnTimeLeft;
    
    @Override
    public int slots() {
        return 3;
    }
    
    @Override
    public ItemStack getStack(int index) {
        return stacks[index];
    }
    
    @Override
    public void setSlotContent(int index, ItemStack stack) {
        stacks[index] = stack;
    }
    
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;//Check for fuel?
    }
    
    @Override
    public void readNBT(NBTCompound c) {
        for (int i = 0; i < stacks.length; i++) {
            if (c.hasKey("h" + i)) {
                stacks[i] = ItemStack.readNBT(c.getCompound("h" + i));
            }
        }
    }
    
    @Override
    public void writeNBT(NBTCompound c) {
        for (int i = 0; i < stacks.length; i++) {
            ItemStack st = stacks[i];
            if (st != null && !st.isEmpty()) {
                c.put("h" + i, ItemStack.writeNBT(st, new NBTCompound()));
            }
        }
    }
}
