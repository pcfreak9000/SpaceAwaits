package de.pcfreak9000.spaceawaits.crafting;

import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public class InventoryCraftingResult implements IInventory {
    private ItemStack[] results = new ItemStack[1];
    
    @Override
    public int slots() {
        return 1;
    }
    
    @Override
    public ItemStack getStack(int index) {
        return results[index];
    }
    
    @Override
    public void setSlotContent(int index, ItemStack stack) {
        results[index] = stack;
    }
    
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }
    
}
