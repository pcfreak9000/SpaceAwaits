package de.pcfreak9000.spaceawaits.registry;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.item.OreDictStack;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class OreDict {
    
    private ListMultimap<String, Item> map = ArrayListMultimap.create();
    private Map<Item, String> reverse = new HashMap<>();
    
    public void addEntry(String key, Item item) {
        map.put(key, item);
        reverse.put(item, key);
    }
    
    public void addEntry(String key, Tile tile) {
        addEntry(key, tile.getItemTile());
    }
    
    public String getKey(Item stack) {
        return reverse.get(stack);
    }
    
    public boolean isItemEqual(String key, Item input) {
        String rvk = getKey(input);
        return Objects.equal(key, rvk);
    }
    
    public boolean isItemEqual(String key, ItemStack input) {
        return isItemEqual(key, input.getItem());
    }
    
    public boolean isItemEqual(OreDictStack stack, ItemStack input) {
        return isItemEqual(stack.getName(), input);
    }
    
    public List<Item> getItemsFor(String key) {
        return Collections.unmodifiableList(map.get(key));
    }
}
