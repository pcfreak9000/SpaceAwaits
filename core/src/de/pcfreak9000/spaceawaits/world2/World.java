package de.pcfreak9000.spaceawaits.world2;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.world.WorldBounds;
import de.pcfreak9000.spaceawaits.world.light.AmbientLightProvider;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public abstract class World {
    
    private WorldBounds worldBounds;
    
    protected final IChunkProvider chunkProvider;
    
    private AmbientLightProvider ambientLightProvider;
    
    private Engine ecsEngine;
    
    public World(WorldPrimer primer) {
        this.ecsEngine = new Engine();
        setupECS(primer, ecsEngine);
        this.worldBounds = primer.getWorldBounds();
        this.ambientLightProvider = primer.getLightProvider();
        
        this.chunkProvider = createChunkProvider(primer);
    }
    
    protected abstract void setupECS(WorldPrimer primer, Engine ecs);
    
    protected abstract IChunkProvider createChunkProvider(WorldPrimer primer);
    
    public void update(float dt) {
        this.ecsEngine.update(dt);
        this.chunkProvider.unloadQueued();
    }
    
    public WorldBounds getBounds() {
        return worldBounds;
    }
    
    public AmbientLightProvider getLightProvider() {
        return ambientLightProvider;
    }
    
    public void setTile(int tx, int ty, Tile tile) {
        
    }
    
    public Tile getTile(int tx, int ty) {
        return null;
    }
    
    public void spawnEntity(Entity entity) {
        
    }
    
    public void despawnEntity(Entity entity) {
        
    }
}
