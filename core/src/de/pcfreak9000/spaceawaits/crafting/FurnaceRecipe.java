package de.pcfreak9000.spaceawaits.crafting;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.registry.OreDict;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

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
    
    public FurnaceRecipe(Item result, Object input) {
        this(new ItemStack(result), input);
    }
    
    public FurnaceRecipe(Tile result, Object input) {
        this(new ItemStack(result), input);
    }
    
    public FurnaceRecipe(ItemStack result, Object input) {
        this(result, input, DEFAULT_BURNTIME);
    }
    
    public FurnaceRecipe(ItemStack result, Object obj, float burntime) {
        this.result = result;
        this.burntime = burntime;
        if (obj instanceof ItemStack) {
            //copy the itemstack fitst or use it directly??
            input = obj;
        } else if (obj instanceof Item) {
            input = new ItemStack((Item) obj);
        } else if (obj instanceof Tile) {
            input = new ItemStack((Tile) obj);
        } else if (obj instanceof String) {
            input = new OreDictStack((String) obj, 1);
        } else if (obj instanceof OreDictStack) {
            //dynamic dictionary stuff
            input = obj;
        } else {
            throw new IllegalArgumentException();
        }
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
            return OreDict.isItemEqual(ods, actualInput) && actualInput.getCount() >= ods.getCount();
        }
        return false;
    }
}
