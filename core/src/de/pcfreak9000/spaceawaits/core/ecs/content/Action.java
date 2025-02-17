package de.pcfreak9000.spaceawaits.core.ecs.content;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

public interface Action {
    
    boolean isContinuous();
    
    Object getInputKey();
    
    boolean handle(float mousex, float mousey, Engine world, Entity source);
    
    default boolean handleRelease(float x, float y, Engine world, Entity e) {
        return false;
    }
}
