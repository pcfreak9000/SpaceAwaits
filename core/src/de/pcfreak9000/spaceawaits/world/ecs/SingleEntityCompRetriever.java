package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

@Deprecated
public class SingleEntityCompRetriever<T extends Component> {
    
    private final ComponentMapper<T> mapper;
    private final Class<T> clazz;
    private ImmutableArray<Entity> array;
    
    public SingleEntityCompRetriever(Class<T> clazz) {
        this.mapper = ComponentMapper.getFor(clazz);
        this.clazz = clazz;
    }
    
    public void addedToEngine(Engine e) {
        array = e.getEntitiesFor(Family.all(clazz).get());
    }
    
    public void removedFromEngine() {
        array = null;
    }
    
    public T get() {
        return mapper.get(array.first());
    }
    
}
