package de.pcfreak9000.spaceawaits.crafting;

import java.util.HashMap;
import java.util.Map;

import de.pcfreak9000.spaceawaits.content.InventoryCrafting;
import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.InvUtil;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class ShapedRecipe implements IRecipe {
    
    private ItemStack result;
    private Object[] inputs;
    private int width, height;
    
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
        int CRAFT_GRID_WIDTH = 3;
        int CRAFT_GRID_HEIGHT = 3;
        for (int x = 0; x <= CRAFT_GRID_WIDTH - width; x++) {
            for (int y = 0; y <= CRAFT_GRID_HEIGHT - height; y++) {
                if (checkMatch((InventoryCrafting) inventory, x, y)) {
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
                } else if (input instanceof String) {
                    //dynamic dict stuff
                }
            }
        }
        return true;
    }
    
    @Override
    public ItemStack craft(IInventory inventory) {
        for (Object in : inputs) {
            InvUtil.removeItemCount(inventory, (ItemStack) in);
        }
        return result.cpy();
    }
    
    @Override
    public ItemStack getResult() {
        return result;
    }
    
}
