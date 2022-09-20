package de.pcfreak9000.spaceawaits.crafting;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;

public class FurnaceRecipe {
    
    private static final float DEFAULT_BURNTIME = 5f;
    
    //************ Maybe put this in a dedicated class? **************
    
    private static final Array<FurnaceRecipe> recipes = new Array<>();
    private static final ImmutableArray<FurnaceRecipe> recipesImmutable = new ImmutableArray<>(recipes);
    
    //    public static void add(ItemStack result, ItemStack... inpts) {
    //        add(new SimpleRecipe(result, inpts));
    //    }
    
    public static void add(FurnaceRecipe sr) {
        recipes.add(sr);
    }
    
    public static ImmutableArray<FurnaceRecipe> getRecipes() {
        return recipesImmutable;
    }
    
    //****************************************************************
    
    private ItemStack result;
    private ItemStack input;
    private float burntime;
    
    public FurnaceRecipe(ItemStack result, ItemStack input) {
        this(result, input, DEFAULT_BURNTIME);
    }
    
    public FurnaceRecipe(ItemStack result, ItemStack input, float burntime) {
        this.result = result;
        this.input = input;
        this.burntime = burntime;
    }
    
    public ItemStack getCraftingResult() {
        return result.cpy();
    }
    
    public ItemStack getResult() {
        return result;
    }
    
    public ItemStack getInput() {
        return input;
    }
    
    public float getBurntime() {
        return burntime;
    }
    
}
