package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.Entity;

public interface WorldEntityFactory {
 
    Entity createEntity();
    
    //serialize/deserialize entities here?
}
