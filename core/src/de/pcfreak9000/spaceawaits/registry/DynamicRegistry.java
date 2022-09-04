package de.pcfreak9000.spaceawaits.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;

public class DynamicRegistry implements INBTSerializable {
    
    private Multimap<String, String> regensStuff = HashMultimap.create();
    private Map<String, String> reverse = new HashMap<>();
    
    public void register(String regen, String recipe) {
        String before = reverse.put(recipe, regen);
        if (before != null) {
            regensStuff.remove(before, recipe);
        }
        regensStuff.put(regen, recipe);
    }
    
    public String getRegenId(String recipe) {
        return reverse.get(recipe);
    }
    
    @Override
    public void readNBT(NBTCompound nbt) {
        for (String k : nbt.keySet()) {
            register(nbt.getString(k), k);
        }
    }
    
    @Override
    public void writeNBT(NBTCompound nbt) {
        for (Entry<String, String> e : reverse.entrySet()) {
            nbt.putString(e.getKey(), e.getValue());
        }
    }
    
}
