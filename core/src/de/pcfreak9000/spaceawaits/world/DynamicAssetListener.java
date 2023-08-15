package de.pcfreak9000.spaceawaits.world;

import java.util.function.Function;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;

import de.pcfreak9000.spaceawaits.core.DynamicAsset;

//What about hot reload of resources?
//Perhaps create an annotation for Fields to just register clazz+assetRetriever directly?
public class DynamicAssetListener<T extends Component> implements EntityListener {
    
    private ComponentMapper<T> mapper;
    private Function<T, Object> assetRetriever;
    private Family family;
    
    public DynamicAssetListener(Class<T> clazz, Function<T, Object> assetRetriever) {
        this.mapper = ComponentMapper.getFor(clazz);
        this.assetRetriever = assetRetriever;
        this.family = Family.all(clazz).get();
    }
    
    public Family getFamily() {
        return family;
    }
    
    @Override
    public void entityAdded(Entity entity) {
        Object tocheck = assetRetriever.apply(mapper.get(entity));
        if (tocheck instanceof DynamicAsset) {
            ((DynamicAsset) tocheck).create();
        }
    }
    
    @Override
    public void entityRemoved(Entity entity) {
        Object tocheck = assetRetriever.apply(mapper.get(entity));
        if (tocheck instanceof DynamicAsset) {
            ((DynamicAsset) tocheck).dispose();
        }
    }
    
}
