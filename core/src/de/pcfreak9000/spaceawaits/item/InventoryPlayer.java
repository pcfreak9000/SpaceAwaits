package de.pcfreak9000.spaceawaits.item;

public class InventoryPlayer implements IInventory {
    
    private ItemStack[] hotbar = new ItemStack[9];
    
    @Override
    public int slots() {
        return 9;
    }
    
    @Override
    public ItemStack getStack(int index) {
        return hotbar[index];
    }
    
    @Override
    public ItemStack removeStack(int index) {
        ItemStack s = hotbar[index];
        hotbar[index] = null;
        return s;
    }
    
    @Override
    public void setSlotContent(int index, ItemStack stack) {
        hotbar[index] = stack;
    }
    
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }
    
}
