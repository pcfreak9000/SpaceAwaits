package de.pcfreak9000.spaceawaits.generation;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

public class GenerationParameters {
    private ClassToInstanceMap<GenerationDataComponent> components;
    
    public GenerationParameters() {
        this.components = MutableClassToInstanceMap.create();
    }
    
    //maybe change this and allow the Class to be a supertype of T
    public <T extends GenerationDataComponent> void setComponent(Class<T> clazz, T comp) {
        this.components.put(clazz, comp);
    }
    
    public <T extends GenerationDataComponent> T getComponent(Class<T> clazz) {
        T data = this.components.getInstance(clazz);
        return data;
    }
    
    public boolean hasComponent(Class<? extends GenerationDataComponent> clazz) {
        return this.components.containsKey(clazz);
    }
}
