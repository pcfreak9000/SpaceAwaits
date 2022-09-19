package de.pcfreak9000.spaceawaits.crafting;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.InvUtil;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public class SimpleRecipe {
    
    //************ Maybe put this in a dedicated class? **************
    
    private static final Array<SimpleRecipe> recipes = new Array<>();
    private static final ImmutableArray<SimpleRecipe> recipesImmutable = new ImmutableArray<>(recipes);
    
    public static void add(ItemStack result, ItemStack... inpts) {
        add(new SimpleRecipe(result, inpts));
    }
    
    public static void add(SimpleRecipe sr) {
        recipes.add(sr);
    }
    
    public static ImmutableArray<SimpleRecipe> getRecipes() {
        return recipesImmutable;
    }
    
    //****************************************************************
    
    private final ItemStack result;
    private final ItemStack[] inputs;
    
    public SimpleRecipe(ItemStack result, ItemStack... inputs) {
        this.result = result;
        this.inputs = inputs;
    }
    
    public ItemStack getResult() {
        return result;
    }
    
    public boolean matches(IInventory inventory) {
        for (ItemStack in : inputs) {
            if (!InvUtil.containsItemCount(inventory, in)) {
                return false;
            }
        }
        return true;
    }
    
    public ItemStack craftFromInventory(IInventory inv) {
        for (ItemStack in : inputs) {
            InvUtil.removeItemCount(inv, in);
        }
        return result.cpy();
    }
    
}
