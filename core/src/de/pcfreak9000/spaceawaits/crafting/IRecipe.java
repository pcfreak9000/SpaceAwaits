package de.pcfreak9000.spaceawaits.crafting;

import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public interface IRecipe {
       
    boolean matches(IInventory inventory);
    
    ItemStack craft(IInventory inventory);
    
    ItemStack getResult();
}
