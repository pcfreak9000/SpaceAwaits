package de.pcfreak9000.spaceawaits.crafting;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.registry.OreDict;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class BlastFurnaceRecipe {
    
    private static final int DEFAULT_BURNTIME = 4 * 60;
    
    //************ Maybe put this in a dedicated class? **************
    
    private static final Array<BlastFurnaceRecipe> recipes = new Array<>();
    private static final ImmutableArray<BlastFurnaceRecipe> recipesImmutable = new ImmutableArray<>(recipes);
    
    //    public static void add(ItemStack result, ItemStack... inpts) {
    //        add(new SimpleRecipe(result, inpts));
    //    }
    
    public static void add(BlastFurnaceRecipe sr) {
        recipes.add(sr);
    }
    
    public static ImmutableArray<BlastFurnaceRecipe> getRecipes() {
        return recipesImmutable;
    }
    
    //****************************************************************
    
    private ItemStack result;
    private Object input;
    private int burntime;
    
    public BlastFurnaceRecipe(Item result, Object input) {
        this(new ItemStack(result), input);
    }
    
    public BlastFurnaceRecipe(Tile result, Object input) {
        this(new ItemStack(result), input);
    }
    
    public BlastFurnaceRecipe(ItemStack result, Object input) {
        this(result, input, DEFAULT_BURNTIME);
    }
    
    public BlastFurnaceRecipe(ItemStack result, Object obj, int burntime) {
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
    
    public int getBurntime() {
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
