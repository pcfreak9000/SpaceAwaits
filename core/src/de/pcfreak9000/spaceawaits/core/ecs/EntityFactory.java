package de.pcfreak9000.spaceawaits.core.ecs;

import com.badlogic.ashley.core.Entity;

public interface EntityFactory {
    
    Entity createEntity();
    
    default Entity recreateEntity() {
        return createEntity();
    }
}
