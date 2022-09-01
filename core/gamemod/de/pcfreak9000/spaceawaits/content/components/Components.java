package de.pcfreak9000.spaceawaits.content.components;

import com.badlogic.ashley.core.ComponentMapper;

public class Components extends de.pcfreak9000.spaceawaits.world.ecs.content.Components {
    public static final ComponentMapper<TreeStateComponent> TREESTATE = ComponentMapper
            .getFor(TreeStateComponent.class);
    
}
