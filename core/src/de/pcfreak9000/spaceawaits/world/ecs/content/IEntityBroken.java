package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.world.World;

public interface IEntityBroken {
    
    void onEntityBroken(World world, Entity entity);
    
}