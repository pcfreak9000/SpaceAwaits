package de.pcfreak9000.spaceawaits.item;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;

public class InventoryPlayer implements IInventory, NBTSerializable {
    
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
    public void readNBT(NBTTag tag) {
        NBTCompound c = (NBTCompound) tag;
        for (int i = 0; i < stacks.length; i++) {
            if (c.hasKey("h" + i)) {
                stacks[i] = ItemStack.readNBT(c.get("h" + i));
            }
        }
    }
    
    @Override
    public NBTTag writeNBT() {
        NBTCompound c = new NBTCompound();
        for (int i = 0; i < stacks.length; i++) {
            ItemStack st = stacks[i];
            if (st != null && !st.isEmpty()) {
                c.put("h" + i, ItemStack.writeNBT(st));
            }
        }
        return c;
    }
    
}
