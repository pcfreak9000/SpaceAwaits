package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

public interface IRenderStrategy {
    default void begin() {
    }
    
    default void end() {
    }
    
    void render(Entity e, float dt);
    
    Family getFamily();
}
