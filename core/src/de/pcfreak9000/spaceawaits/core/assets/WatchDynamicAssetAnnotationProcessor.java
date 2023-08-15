package de.pcfreak9000.spaceawaits.core.assets;

import java.lang.reflect.Field;
import java.util.Set;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.OrderedSet;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;

public class WatchDynamicAssetAnnotationProcessor {
    
    private static OrderedSet<DynamicAssetListener<Component>> stuff;
    
    public static OrderedSet<DynamicAssetListener<Component>> get() {
        if (stuff != null) {
            return stuff;
        }
        Set<Class<?>> stuff = SpaceAwaits.getSpaceAwaits().getClassesWithWatchDynamicAsset();
        OrderedSet<DynamicAssetListener<Component>> out = new OrderedSet<>();
        for (Class<?> cl : stuff) {
            if (!Component.class.isAssignableFrom(cl)) {
                continue;
            }
            Field[] fs = cl.getDeclaredFields();
            for (Field f : fs) {
                if (f.getAnnotation(WatchDynamicAsset.class) == null) {
                    continue;
                }
                f.setAccessible(true);
                DynamicAssetListener<Component> dal = new DynamicAssetListener<Component>((Class<Component>) cl,
                        (comp) -> {
                            try {
                                return f.get(comp);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                out.add(dal);
            }
        }
        WatchDynamicAssetAnnotationProcessor.stuff = out;
        return out;
    }
}
