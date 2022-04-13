package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;

import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.DynamicAssetComponent;

public class DynamicAssetListener implements EntityListener {
    
    private static final Family FAMILY = Family.all(DynamicAssetComponent.class).get();
    
    public void register(Engine engine) {
        engine.addEntityListener(FAMILY, this);
    }
    
    @Override
    public void entityAdded(Entity entity) {
        Components.DYNAMIC_ASSET.get(entity).dynamicAsset.create();
    }
    
    @Override
    public void entityRemoved(Entity entity) {
        Components.DYNAMIC_ASSET.get(entity).dynamicAsset.dispose();
    }
    
}
