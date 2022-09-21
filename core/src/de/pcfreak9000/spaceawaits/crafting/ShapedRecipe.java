package de.pcfreak9000.spaceawaits.crafting;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.item.OreDictStack;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class ShapedRecipe implements IRecipe {
    
    //************ Maybe put this in a dedicated class? **************
    
    private static final Array<ShapedRecipe> recipes = new Array<>();
    //private static final ImmutableArray<ShapedRecipe> recipesImmutable = new ImmutableArray<>(recipes);
    
    public static void add(ShapedRecipe sr) {
        recipes.add(sr);
    }
    
    //TODO maybe IGridRecipe instead?
    public static ShapedRecipe findMatchingRecipe(InventoryCrafting inventoryCrafting) {
        for (ShapedRecipe s : recipes) {
            if (s.matches(inventoryCrafting)) {
                return s;
            }
        }
        return null;
    }
    
    //****************************************************************
    
    private ItemStack result;
    private Object[] inputs;
    private int width, height;
    
    public ShapedRecipe(Item result, Object... recipe) {
        this(new ItemStack(result), recipe);
    }
    
    public ShapedRecipe(Tile result, Object... recipe) {
        this(new ItemStack(result), recipe);
    }
    
    public ShapedRecipe(ItemStack result, Object... recipe) {
        this.result = result;
        String shape = "";
        int index = 0;
        while (recipe[index] instanceof String) {
            String s = (String) recipe[index];
            index++;
            shape += s;
            width = s.length();
            height++;
        }
        if (width * height != shape.length()) {
            throw new IllegalArgumentException("Malformed recipe: shape");
        }
        Map<Character, Object> itemmap = new HashMap<>();
        for (; index < recipe.length; index += 2) {
            Character c = (Character) recipe[index];
            Object obj = recipe[index + 1];
            if (obj instanceof ItemStack) {
                //copy the itemstack fitst or use it directly??
                itemmap.put(c, obj);
            } else if (obj instanceof Item) {
                itemmap.put(c, new ItemStack((Item) obj));
            } else if (obj instanceof Tile) {
                itemmap.put(c, new ItemStack((Tile) obj));
            } else if (obj instanceof String) {
                itemmap.put(c, new OreDictStack((String) obj, 1));
            } else if (obj instanceof OreDictStack) {
                //dynamic dictionary stuff
                itemmap.put(c, obj);
            } else {
                throw new IllegalArgumentException("Malformed recipe: ingredients");
            }
        }
        inputs = new Object[width * height];
        int x = 0;
        for (char chr : shape.toCharArray()) {
            inputs[x++] = itemmap.get(chr);
        }
    }
    
    @Override
    public boolean matches(IInventory inventory) {
        if (!(inventory instanceof InventoryCrafting)) {
            return false;
        }
        InventoryCrafting ic = (InventoryCrafting) inventory;
        int craftgridWidth = ic.getSideSize();
        int craftgridHeight = ic.getSideSize();
        for (int x = 0; x <= craftgridWidth - width; x++) {
            for (int y = 0; y <= craftgridHeight - height; y++) {
                if (checkMatch(ic, x, y)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean checkMatch(InventoryCrafting inv, int x, int y) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int ax = i + x;
                int ay = j + y;
                Object input = inputs[i + j * width];
                ItemStack slot = inv.getStackInXY(ax, ay);
                if (input instanceof ItemStack) {
                    if (!ItemStack.isItemEqual(slot, (ItemStack) input)) {
                        return false;
                    }
                } else if (input instanceof OreDictStack) {
                    if (!GameRegistry.getOreDict().isItemEqual((OreDictStack) input, slot)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    @Override
    public ItemStack getCraftingResult(IInventory inventory) {
        return result.cpy();
    }
    
    @Override
    public ItemStack getResult() {
        return result;
    }
    
}
