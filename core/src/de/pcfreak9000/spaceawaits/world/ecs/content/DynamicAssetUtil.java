package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.Entity;

public class DynamicAssetUtil {
    
    public static void checkAndCreateAsset(Entity e) {
        if (Components.DYNAMIC_ASSET.has(e)) {
            Components.DYNAMIC_ASSET.get(e).dynamicAsset.create();
        }
    }
    
    public static void checkAndDisposeAsset(Entity e) {
        if (Components.DYNAMIC_ASSET.has(e)) {
            Components.DYNAMIC_ASSET.get(e).dynamicAsset.dispose();
        }
    }
}
