package de.pcfreak9000.spaceawaits.world.render.strategy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

public interface IRenderStrategy {
    default void begin() {
    }
    
    default void end() {
    }
    
    default boolean considerGui() {
        return false;
    }
    
    void render(Entity e, float dt);
    
    Family getFamily();
}
