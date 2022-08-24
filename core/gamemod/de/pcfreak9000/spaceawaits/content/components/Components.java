package de.pcfreak9000.spaceawaits.content.components;

import de.pcfreak9000.spaceawaits.registry.GameRegistry;

public class Components extends de.pcfreak9000.spaceawaits.world.ecs.content.Components {
    
    public static void registerComponents() {
        GameRegistry.WORLD_COMPONENT_REGISTRY.register("spaceawaitsComponentTreeState", TreeStateComponent.class);
    }
    
}
