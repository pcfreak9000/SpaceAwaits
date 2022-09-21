package de.pcfreak9000.spaceawaits.crafting;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.InvUtil;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class SimpleRecipe {
    
    //************ Maybe put this in a dedicated class? **************
    
    private static final Array<SimpleRecipe> recipes = new Array<>();
    private static final ImmutableArray<SimpleRecipe> recipesImmutable = new ImmutableArray<>(recipes);
    
    public static void add(ItemStack result, Object... inpts) {
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
    private final Object[] inputs;
    
    public SimpleRecipe(ItemStack result, Object... inputs) {
        if (inputs.length == 0) {
            throw new IllegalArgumentException();
        }
        this.inputs = new Object[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            Object obj = inputs[i];
            if (obj instanceof ItemStack) {
                //copy the itemstack fitst or use it directly??
                this.inputs[i] = obj;
            } else if (obj instanceof Item) {
                this.inputs[i] = new ItemStack((Item) obj);
            } else if (obj instanceof Tile) {
                this.inputs[i] = new ItemStack((Tile) obj);
            } else if (obj instanceof String) {
                this.inputs[i] = new OreDictStack((String) obj, 1);
            } else if (obj instanceof OreDictStack) {
                //dynamic dictionary stuff
                this.inputs[i] = obj;
            } else {
                throw new IllegalArgumentException();
            }
        }
        this.result = result;
    }
    
    public ItemStack getResult() {
        return result;
    }
    
    public boolean matches(IInventory inventory) {
        for (Object in : inputs) {
            if (in instanceof ItemStack) {
                if (!InvUtil.containsItemCount(inventory, (ItemStack) in)) {
                    return false;
                }
            } else if (in instanceof OreDictStack) {
                if (!InvUtil.containsItemCount(inventory, (OreDictStack) in)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public ItemStack craftFromInventory(IInventory inv) {
        for (Object in : inputs) {
            if (in instanceof ItemStack) {
                InvUtil.removeItemCount(inv, (ItemStack) in);
            } else if (in instanceof OreDictStack) {
                InvUtil.removeItemCount(inv, (OreDictStack) in);
            }
        }
        return result.cpy();
    }
    
}
