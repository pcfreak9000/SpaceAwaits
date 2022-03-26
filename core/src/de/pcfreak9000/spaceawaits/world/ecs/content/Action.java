package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.world.World;

public interface Action {
    Object getInputKey();
    
    boolean handle(float mousex, float mousey, World world, Entity source);
}
