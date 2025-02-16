package de.pcfreak9000.spaceawaits.core.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

public class SystemCache<T extends EntitySystem> {
    
    private final Class<T> clazz;
    
    private T t;
    private Engine lastEngine;
    
    public SystemCache(Class<T> clazz) {
        this.clazz = clazz;
    }
    
    //doesn't handle a change of engine, but that shouldn't happen. -> Well it does, if systemcache is static -> Also what if system t or the caller are disconnected? but whatever
    public T get(Engine e) {
        if (t == null || e != lastEngine) {
            t = e.getSystem(clazz);
            lastEngine = e;
        }
        return t;
    }
    
}
