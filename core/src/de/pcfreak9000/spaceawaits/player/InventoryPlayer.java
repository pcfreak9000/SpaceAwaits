package de.pcfreak9000.spaceawaits.player;

import java.util.Arrays;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;

public class InventoryPlayer implements IInventory, INBTSerializable {
    
    private ItemStack[] stacks = new ItemStack[9 * 4];
    private int selected;
    
    public void setSelectedSlot(int selected) {
        this.selected = selected;
    }
    
    public int getSelectedSlot() {
        return selected;
    }
    
    public ItemStack getSelectedStack() {
        return getStack(getSelectedSlot());
    }
    
    @Override
    public int slots() {
        return stacks.length;
    }
    
    @Override
    public ItemStack getStack(int index) {
        return stacks[index];
    }
    
    @Override
    public ItemStack removeStack(int index) {
        ItemStack s = stacks[index];
        stacks[index] = null;
        return s;
    }
    
    @Override
    public void setSlotContent(int index, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            stack = null;
        }
        stacks[index] = stack;
    }
    
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
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
    
    @Override
    public String toString() {
        return Arrays.toString(this.stacks);
    }
    
}
