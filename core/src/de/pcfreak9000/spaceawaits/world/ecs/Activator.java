package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.world.World;

public interface Activator {
    
    boolean isContinuous();
    
    Object getInputKey();
    
    /**
     * 
     * @param mousex
     * @param mousey
     * @param entity the activated entity
     * @param world
     * @param source the entity that activated this Activator
     * @return
     */
    boolean handle(float mousex, float mousey, Entity entity, World world, Entity source);
    
    //    default boolean handleRelease(float x, float y, Entity e, World world, Entity playerEntity) {
    //        return false;
    //    }
}
