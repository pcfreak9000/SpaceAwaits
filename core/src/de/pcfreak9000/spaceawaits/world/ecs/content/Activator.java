package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.world.World;

public interface Activator {
    
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
}
