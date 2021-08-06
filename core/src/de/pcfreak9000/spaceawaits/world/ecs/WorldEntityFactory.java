package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Entity;

public interface WorldEntityFactory {
    
    Entity createEntity();
    
    default Entity recreateEntity() {
        return createEntity();
    }
}
