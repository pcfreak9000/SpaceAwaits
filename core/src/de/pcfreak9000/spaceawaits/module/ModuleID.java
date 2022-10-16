package de.pcfreak9000.spaceawaits.module;

import java.util.HashMap;
import java.util.Map;

public class ModuleID {
    
    private static Map<Class<? extends IModule>, ModuleID> ids = new HashMap<>();
    private static int counter = 0;
    
    public static ModuleID getFor(Class<? extends IModule> clazz) {
        ModuleID id = ids.get(clazz);
        if (id == null) {
            id = new ModuleID(++counter, clazz);
            ids.put(clazz, id);
        }
        return id;
    }
    
    private final int id;
    private final Class<? extends IModule> clazz;
    
    private ModuleID(int id, Class<? extends IModule> clazz) {
        this.id = id;
        this.clazz = clazz;
    }
    
    public Class<? extends IModule> getModuleClass() {
        return this.clazz;
    }
    
    public int getIndex() {
        return id;
    }
    
}
