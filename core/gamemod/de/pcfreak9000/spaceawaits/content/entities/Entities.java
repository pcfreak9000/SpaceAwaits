package de.pcfreak9000.spaceawaits.content.entities;

import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.world.ecs.WorldEntityFactory;

public class Entities {
    public static final WorldEntityFactory TREE = new TreeFactory();
    
    public static void registerEntities() {
        Registry.WORLD_ENTITY_REGISTRY.register("tree", TREE);
    }
}
