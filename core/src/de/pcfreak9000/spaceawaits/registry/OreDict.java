package de.pcfreak9000.spaceawaits.registry;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class OreDict {
    
    private ListMultimap<String, Object> map = ArrayListMultimap.create();
    private Map<Object, String> reverse = new HashMap<>();
    
    public void addEntry(String key, Item item) {
        map.put(key, item);
        reverse.put(item, key);
    }
    
    public void addEntry(String key, Tile tile) {
        map.put(key, tile);
        reverse.put(tile, key);
    }
    
    public String getKey(ItemStack stack) {
        return reverse.get(stack);
    }
}
