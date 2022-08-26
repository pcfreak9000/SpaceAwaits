package de.pcfreak9000.spaceawaits.content;

import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public class InventoryCrafting implements IInventory {
    private final ItemStack[] stacks;
    
    public InventoryCrafting(int side) {
        this.stacks = new ItemStack[side * side];
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
        stacks[index] = stack;
    }
    
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }
    
}
