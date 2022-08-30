package de.pcfreak9000.spaceawaits.content;

import de.pcfreak9000.spaceawaits.crafting.CraftingManager;
import de.pcfreak9000.spaceawaits.crafting.InventoryCraftingResult;
import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public class InventoryCrafting implements IInventory {
    private final ItemStack[] stacks;
    private final int size;
    
    private IInventory craftingresult;
    
    public InventoryCrafting(int side) {
        this.stacks = new ItemStack[side * side];
        this.size = side;
        this.craftingresult = new InventoryCraftingResult();
    }
    
    public IInventory getResultInventory() {
        return this.craftingresult;
    }
    
    public ItemStack getStackInXY(int x, int y) {
        return getStack(x + y * size);
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
    public void setSlotContent(int index, ItemStack stack) {
        stacks[index] = stack;
        craftingresult.setSlotContent(0, CraftingManager.instance().tryCraft(this));
    }
    
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }
    
}
