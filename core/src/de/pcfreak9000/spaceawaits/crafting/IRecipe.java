package de.pcfreak9000.spaceawaits.crafting;

import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public interface IRecipe {
       
    boolean matches(IInventory inventory);
    
    //When the crafting actually happens
    ItemStack getCraftingResult(IInventory inventory);
    
    //For information purposes about this recipe
    ItemStack getResult();
}
