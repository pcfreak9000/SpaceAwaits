package de.pcfreak9000.spaceawaits.comp;

import com.badlogic.gdx.utils.ObjectFloatMap;

public class CompositeInventory {
    
    private ObjectFloatMap<String> storage;
    
    public boolean hasComposite(String name) {
        return storage.get(name, 0) > 0;
    }
    
    public float getCompositeAmount(String name) {
        return storage.get(name, 0);
    }
    
    public void putComposite(String name, float amount) {
        //TODO check if that composite exists first
        float current = storage.get(name, 0);
        storage.put(name, amount + current);
    }
}
