package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

public class DynamicAssetUtil {
    private static final ComponentMapper<DynamicAssetComponent> MAPPER = ComponentMapper
            .getFor(DynamicAssetComponent.class);
    
    public static void checkAndCreateAsset(Entity e) {
        if (MAPPER.has(e)) {
            MAPPER.get(e).dynamicAsset.create();
        }
    }
    
    public static void checkAndDisposeAsset(Entity e) {
        if (MAPPER.has(e)) {
            MAPPER.get(e).dynamicAsset.dispose();
        }
    }
}
