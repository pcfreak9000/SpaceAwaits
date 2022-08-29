package de.pcfreak9000.spaceawaits.crafting;

import java.util.HashMap;
import java.util.Map;

import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.InvUtil;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class ShapedRecipe implements IRecipe {
    
    private ItemStack result;
    private ItemStack[] inputs;
    private int width, height;
    
    public ShapedRecipe(ItemStack result, Object... recipe) {
        this.result = result;
        String shape = "";
        int index = 0;
        while (recipe[index] instanceof String) {
            String s = (String) recipe[index];
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
            } else {
                throw new IllegalArgumentException("Malformed recipe: ingredients");
            }
        }
    }
    
    @Override
    public boolean matches(IInventory inventory) {
        return false;
    }
    
    @Override
    public ItemStack craft(IInventory inventory) {
        for (ItemStack in : inputs) {
            InvUtil.removeItemCount(inventory, in);
        }
        return result.cpy();
    }
    
    @Override
    public ItemStack getResult() {
        return result;
    }
    
}
