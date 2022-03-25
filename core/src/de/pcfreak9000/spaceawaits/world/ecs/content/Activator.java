package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.world.World;

public interface Activator {
    
    Object getInputKey();
    
    boolean handle(float mousex, float mousey, Entity entity, World world);
}
