package de.pcfreak9000.spaceawaits.crafting;

import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public class InventoryCraftingResult implements IInventory {
    private ItemStack[] results = new ItemStack[1];
    
    private InventoryCrafting parent;
    
    public InventoryCraftingResult(InventoryCrafting parent) {
        this.parent = parent;
    }
    
    @Override
    public int slots() {
        return 1;
    }
    
    @Override
    public ItemStack getStack(int index) {
        return results[index];
    }
    
    @Override
    public ItemStack decrStackSize(int slot, int remov) {
        return parent.tryCraft(remov);
    }
    
    @Override
    public ItemStack removeStack(int index) {
        return parent.tryCraft(1);
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
