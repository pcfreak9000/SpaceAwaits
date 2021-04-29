package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.core.Player;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.ecs.entity.ChunkMarkerComponent;
import de.pcfreak9000.spaceawaits.world.light.AmbientLightProvider;
import de.pcfreak9000.spaceawaits.world.tile.Chunk;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public abstract class World {
    
    private WorldBounds worldBounds;
    
    protected final IChunkProvider chunkProvider;
    protected final IUnchunkProvider unchunkProvider;
    
    private AmbientLightProvider ambientLightProvider;
    
    protected final Engine ecsEngine;
    
    public World(WorldPrimer primer) {
        //initialize fields
        this.ecsEngine = new Engine();
        
        //do priming stuff
        this.worldBounds = primer.getWorldBounds();
        this.ambientLightProvider = primer.getLightProvider();
        
        this.unchunkProvider = createUnchunkProvider(primer);
        this.chunkProvider = createChunkProvider(primer);
        //setup
        finishSetup(primer, ecsEngine);
    }
    
    protected abstract void finishSetup(WorldPrimer primer, Engine ecs);
    
    protected abstract IChunkProvider createChunkProvider(WorldPrimer primer);
    
    protected abstract IUnchunkProvider createUnchunkProvider(WorldPrimer primer);
    
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
    
    protected void addChunk(Chunk c) {
        c.addToECS(ecsEngine);
    }
    
    protected void removeChunk(Chunk c) {
        c.removeFromECS(ecsEngine);
    }
    
    public Tile setTile(int tx, int ty, TileLayer layer, Tile tile) {
        if (!getBounds().inBounds(tx, ty)) {
            return null;
        }
        Chunk c = chunkProvider.getChunk(Chunk.toGlobalChunk(tx), Chunk.toGlobalChunk(ty));
        return c.setTile(tx, ty, layer, tile);
    }
    
    public Tile getTile(int tx, int ty, TileLayer layer) {
        if (!getBounds().inBounds(tx, ty)) {
            return null;
        }
        Chunk c = chunkProvider.getChunk(Chunk.toGlobalChunk(tx), Chunk.toGlobalChunk(ty));
        return c.getTile(tx, ty, layer);
    }
    
    public void joinWorld(Player player) {
        ecsEngine.addEntity(player.getPlayerEntity());
    }
    
    public void spawnEntity(EntityImproved entityImproved) {
        
    }
    
    public void despawnEntity(EntityImproved entityImproved) {
        
    }
    
    //This hopefully works properly... it seems like it doesn't
    public void adjustChunk(Entity e, ChunkMarkerComponent c, TransformComponent t) {
        int supposedChunkX = Chunk.toGlobalChunkf(t.position.x);
        int supposedChunkY = Chunk.toGlobalChunkf(t.position.y);
        if (c.currentChunk == null) {
            throw new NullPointerException();
        } else if (supposedChunkX != c.currentChunk.getGlobalChunkX()
                || supposedChunkY != c.currentChunk.getGlobalChunkY()) {
            Chunk newchunk = chunkProvider.getChunk(supposedChunkX, supposedChunkY);
            //If for some reason the new chunk doesn't exist, keep the old link
            if (newchunk != null) {
                c.currentChunk.removeEntity(e);
                newchunk.addEntity(e);
                if (!newchunk.isActive()) {
                    //Calling this method means the supplied entity is updating, but after the switch it might not be supposed to be anymore so it will be removed
                    ecsEngine.removeEntity(e);
                }
            }
        }
    }
    
    public void unloadAll() {
        this.chunkProvider.queueUnloadAll();
        this.chunkProvider.unloadQueued();
        this.unchunkProvider.unload();
    }
}
