package de.pcfreak9000.spaceawaits.crafting;

import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public class InventoryGridCrafting implements IInventory {
    private final ItemStack[] stacks;
    private final int size;
    
    private IInventory craftingresult;
    
    private IRecipe currentRecipe;
    private ItemStack result;
    
    public InventoryGridCrafting(int side) {
        this.stacks = new ItemStack[side * side];
        this.size = side;
        this.craftingresult = new InventoryCraftingResult(this);
    }
    
    public IInventory getResultInventory() {
        return this.craftingresult;
    }
    
    public int getSideSize() {
        return size;
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
    
    public ItemStack tryCraft(int count) {
        if (currentRecipe == null) {
            return null;
        }
        ItemStack ret = result;
        int recipeMult = ret.getCount();
        int canCraftCount = count;
        for (ItemStack content : stacks) {
            if (!ItemStack.isEmptyOrNull(content)) {
                canCraftCount = Math.min(canCraftCount, content.getCount());
            }
        }
        //FIXME meh... 
        int outcount = recipeMult * canCraftCount;
        outcount = Math.min(ret.getMax(), outcount);
        for (int i = 0; i < slots(); i++) {
            decrStackSize(i, canCraftCount);
        }
        ret.setCount(outcount);
        return ret;
    }
    
    private void updateCurrentRecipe() {
        if (currentRecipe != null) {
            if (currentRecipe.matches(this)) {
                return;
            }
        }
        currentRecipe = ShapedRecipe.findMatchingRecipe(this);//TODO shapeless recipes etc, also more general stuff for other crafting stuff
        if (currentRecipe != null) {
            result = currentRecipe.getCraftingResult(this);
            craftingresult.setSlotContent(0, result);
        } else {
            result = null;
            craftingresult.setSlotContent(0, null);
        }
    }
    
    @Override
    public void setSlotContent(int index, ItemStack stack) {
        stacks[index] = stack;
        updateCurrentRecipe();
    }
    
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }
    
}
