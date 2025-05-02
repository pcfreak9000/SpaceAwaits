package de.pcfreak9000.spaceawaits.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.core.assets.WatchDynamicAsset;
import de.pcfreak9000.spaceawaits.mod.Modloader;
import de.pcfreak9000.spaceawaits.serialize.NBTSerialize;

public class ReflectionManager {
    private Set<Class<?>> classesWithSerialize = new LinkedHashSet<>();
    private Set<Class<?>> classesWithSerializeIm = Collections.unmodifiableSet(classesWithSerialize);
    private Set<Class<? extends Component>> componentsWithSerialize = new LinkedHashSet<>();
    private Set<Class<? extends Component>> componentsWithSerializeIm = Collections
            .unmodifiableSet(componentsWithSerialize);
    private Map<String, Class<? extends Component>> componentsByKey = new HashMap<>();
    
    private Set<Class<?>> classesWithWatchDynamicAsset = new LinkedHashSet<>();
    
    private Modloader modloader;
    
    public ReflectionManager(Modloader modloader) {
        this.modloader = modloader;
    }
    
    public void collectAnnotationsEtc() {
        Reflections refl = new Reflections("de.pcfreak9000.spaceawaits");
        classesWithSerialize.addAll(refl.getTypesAnnotatedWith(NBTSerialize.class));
        classesWithSerialize.addAll(this.modloader.getModClassesWithSerialize());
        classesWithWatchDynamicAsset.addAll(refl.getTypesAnnotatedWith(WatchDynamicAsset.class));
        classesWithWatchDynamicAsset.addAll(this.modloader.getModClassesWithWatchDynamicAsset());
        for (Class<?> c : classesWithSerialize) {
            if (Component.class.isAssignableFrom(c)) {
                componentsWithSerialize.add((Class<? extends Component>) c);
                componentsByKey.put(c.getAnnotation(NBTSerialize.class).key(), (Class<? extends Component>) c);
            }
        }
    }
    
    public Set<Class<?>> getClassesWithWatchDynamicAsset() {
        return classesWithWatchDynamicAsset;
    }
    
    public Set<Class<?>> getClassesWithSerialize() {
        return classesWithSerializeIm;
    }
    
    public Set<Class<? extends Component>> getComponentsWithSerialize() {
        return componentsWithSerializeIm;
    }
    
    public Class<? extends Component> getComponentByKey(String key) {
        return componentsByKey.get(key);
    }
    
}
