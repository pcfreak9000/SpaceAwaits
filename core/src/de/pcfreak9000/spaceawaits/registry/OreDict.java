package de.pcfreak9000.spaceawaits.registry;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.pcfreak9000.spaceawaits.crafting.OreDictStack;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class OreDict {
    
    private OreDict() {
    }
    
    private static ListMultimap<String, Item> map = ArrayListMultimap.create();
    private static Map<Item, String> reverse = new HashMap<>();
    
    public static void addEntry(String key, Item item) {
        map.put(key, item);
        reverse.put(item, key);
    }
    
    public static void addEntry(String key, Tile tile) {
        addEntry(key, tile.getItemTile());
    }
    
    public static String getKey(Item stack) {
        return reverse.get(stack);
    }
    
    public static boolean isItemEqual(String key, Item input) {
        if (input == null) {
            return false;
        }
        String rvk = getKey(input);
        return Objects.equal(key, rvk);
    }
    
    public static boolean isItemEqual(String key, ItemStack input) {
        if (ItemStack.isEmptyOrNull(input)) {
            return false;
        }
        return isItemEqual(key, input.getItem());
    }
    
    //Check the oredictstack for null as well?
    public static boolean isItemEqual(OreDictStack stack, ItemStack input) {
        return isItemEqual(stack.getName(), input);
    }
    
    public static List<Item> getItemsFor(String key) {
        return Collections.unmodifiableList(map.get(key));
    }
}
