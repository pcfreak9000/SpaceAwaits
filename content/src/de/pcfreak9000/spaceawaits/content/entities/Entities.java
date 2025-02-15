package de.pcfreak9000.spaceawaits.content.entities;

import de.pcfreak9000.spaceawaits.core.ecs.EntityFactory;
import de.pcfreak9000.spaceawaits.registry.Registry;

public class Entities {
    public static final EntityFactory TREE = new TreeFactory();
    
    public static void registerEntities() {
        Registry.WORLD_ENTITY_REGISTRY.register("tree", TREE);
    }
}
