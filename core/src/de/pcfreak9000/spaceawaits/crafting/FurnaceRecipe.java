package de.pcfreak9000.spaceawaits.crafting;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.item.OreDictStack;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;

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
    private Object input;
    private float burntime;
    
    public FurnaceRecipe(ItemStack result, ItemStack input) {
        this(result, input, DEFAULT_BURNTIME);
    }
    
    public FurnaceRecipe(ItemStack result, ItemStack input, float burntime) {
        this.result = result;
        this.input = input;
        this.burntime = burntime;
    }
    
    public FurnaceRecipe(ItemStack result, OreDictStack input) {
        this(result, input, DEFAULT_BURNTIME);
    }
    
    public FurnaceRecipe(ItemStack result, OreDictStack input, float burntime) {
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
    
    public float getBurntime() {
        return burntime;
    }
    
    public int getInputCount() {
        return this.input instanceof ItemStack ? ((ItemStack) input).getCount() : ((OreDictStack) input).getCount();
    }
    
    public boolean matches(ItemStack actualInput) {
        if (this.input instanceof ItemStack) {
            ItemStack is = (ItemStack) this.input;
            return ItemStack.isItemEqual(is, actualInput) && actualInput.getCount() >= is.getCount();
        } else if (this.input instanceof OreDictStack) {
            OreDictStack ods = (OreDictStack) input;
            return GameRegistry.getOreDict().isItemEqual(ods, actualInput) && actualInput.getCount() >= ods.getCount();
        }
        return false;
    }
}
