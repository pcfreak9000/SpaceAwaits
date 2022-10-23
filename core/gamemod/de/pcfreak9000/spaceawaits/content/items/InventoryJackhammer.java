package de.pcfreak9000.spaceawaits.content.items;

import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.module.IModuleEnergy;

public class InventoryJackhammer implements IInventory {
    
    private ItemStack[] stacks = new ItemStack[1];
    
    @Override
    public int slots() {
        return stacks.length;
    }
    
    @Override
    public ItemStack getStack(int index) {
        return stacks[0];
    }
    
    @Override
    public void setSlotContent(int index, ItemStack stack) {
        stacks[0] = stack;
    }
    
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return !ItemStack.isEmptyOrNull(stack) && stack.getItem().hasModule(IModuleEnergy.ID);
    }
    
}
