package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.world.light.AmbientLightProvider;

public class Global {
    
    private AmbientLightProvider lightProvider;
    
    private Array<Entity> entities;
    private ImmutableArray<Entity> entitiesImmutable;
    
    public Global() {
        this.entities = new Array<>();
        this.entitiesImmutable = new ImmutableArray<>(this.entities);
        this.lightProvider = AmbientLightProvider.constant(Color.WHITE);
    }
    
    public ImmutableArray<Entity> getEntities() {
        return entitiesImmutable;
    }
    
    public AmbientLightProvider getLightProvider() {
        return lightProvider;
    }
    
    public void setLightProvider(AmbientLightProvider provider) {
        this.lightProvider = provider;
    }
    
    public void addEntity(Entity e) {
        this.entities.add(e);
    }
    
    public void removeEntity(Entity e) {
        this.entities.removeValue(e, true);
    }
    
}
