package de.pcfreak9000.spaceawaits.content.components;

import com.badlogic.ashley.core.ComponentMapper;

import de.pcfreak9000.spaceawaits.registry.GameRegistry;

public class Components extends de.pcfreak9000.spaceawaits.world.ecs.content.Components {
    public static final ComponentMapper<TreeStateComponent> TREESTATE = ComponentMapper
            .getFor(TreeStateComponent.class);
    
    public static void registerComponents() {
        GameRegistry.WORLD_COMPONENT_REGISTRY.register("spaceawaitsComponentTreeState", TreeStateComponent.class);
    }
    
}
