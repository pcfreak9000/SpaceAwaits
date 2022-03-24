package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

public class SystemCache<T extends EntitySystem> {
    
    private final Class<T> clazz;
    
    private T t;
    
    public SystemCache(Class<T> clazz) {
        this.clazz = clazz;
    }
    
    //    public void addedToEngine(Engine e) {
    //        this.engine = e;
    //    }
    //    
    //    public void removedFromEngine() {
    //        this.engine = null;
    //        this.t = null;
    //    }
    //    
    //    public T get() {
    //        if (t == null) {
    //            t = engine.getSystem(clazz);
    //        }
    //        return t;
    //    }
    
    //doesn't handle a change of engine, but that shouldn't happen. Also what if system t or the caller are disconnected? but whatever
    public T get(Engine e) {
        if (t == null) {
            t = e.getSystem(clazz);
        }
        return t;
    }
    
}
