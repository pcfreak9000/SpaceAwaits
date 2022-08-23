package de.pcfreak9000.spaceawaits.crafting;

import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.InvUtil;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public class SimpleRecipe implements IRecipe {
    
    private final ItemStack result;
    private final ItemStack[] inputs;
    
    public SimpleRecipe(ItemStack result, ItemStack... inputs) {
        this.result = result;
        this.inputs = inputs;
    }
    
    @Override
    public ItemStack getResult() {
        return result;
    }
    
    @Override
    public boolean matches(IInventory inventory) {
        for (ItemStack in : inputs) {
            if (!InvUtil.containsItemCount(inventory, in)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public ItemStack craft(IInventory inventory) {
        for (ItemStack in : inputs) {
            InvUtil.removeItemCount(inventory, in);
        }
        return result.cpy();
    }
    
}
