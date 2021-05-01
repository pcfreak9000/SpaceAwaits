package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.core.Player;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.ecs.entity.ChunkMarkerComponent;
import de.pcfreak9000.spaceawaits.world.light.AmbientLightProvider;
import de.pcfreak9000.spaceawaits.world.physics.IRaycastCallback;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsSystemBox2D;
import de.pcfreak9000.spaceawaits.world.tile.Chunk;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.TileEntity;

public abstract class World {
    
    private WorldBounds worldBounds;
    private final long seed;
    
    protected final IChunkProvider chunkProvider;
    protected final IUnchunkProvider unchunkProvider;
    
    private AmbientLightProvider ambientLightProvider;
    
    protected final Engine ecsEngine;
    
    public World(WorldPrimer primer, long seed) {
        //initialize fields
        this.seed = seed;
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
    
    /**
     * Sets a tile.
     * 
     * @param tx    tile x
     * @param ty    tile y
     * @param layer tilelayer
     * @param tile  the new Tile
     * @return the old tile or null if nothing changed (out of bounds or not loaded)
     */
    public Tile setTile(int tx, int ty, TileLayer layer, Tile tile) {
        if (getBounds().inBounds(tx, ty)) {
            Chunk c = chunkProvider.getChunk(Chunk.toGlobalChunk(tx), Chunk.toGlobalChunk(ty));
            if (c != null) {
                Tile old = c.setTile(tx, ty, layer, tile);
                notifyNeighbours(tile, old, tx, ty, layer);
                return old;
            }
        }
        return null;
    }
    
    private void notifyNeighbours(Tile tile, Tile old, int tx, int ty, TileLayer layer) {
        getTile(tx + 1, ty, layer).onNeighbourChange(this, tx + 1, ty, tile, old, tx, ty);
        getTile(tx, ty + 1, layer).onNeighbourChange(this, tx, ty + 1, tile, old, tx, ty);
        getTile(tx - 1, ty, layer).onNeighbourChange(this, tx - 1, ty, tile, old, tx, ty);
        getTile(tx, ty - 1, layer).onNeighbourChange(this, tx, ty - 1, tile, old, tx, ty);
    }
    
    public Tile getTile(int tx, int ty, TileLayer layer) {
        if (getBounds().inBounds(tx, ty)) {
            Chunk c = chunkProvider.getChunk(Chunk.toGlobalChunk(tx), Chunk.toGlobalChunk(ty));
            if (c != null) {
                return c.getTile(tx, ty, layer);
            }
        }
        return Tile.EMPTY;
    }
    
    public TileEntity getTileEntity(int tx, int ty, TileLayer layer) {
        if (getBounds().inBounds(tx, ty)) {
            Chunk c = chunkProvider.getChunk(Chunk.toGlobalChunk(tx), Chunk.toGlobalChunk(ty));
            if (c != null) {
                return c.getTileEntity(tx, ty, layer);
            }
        }
        return null;
    }
    
    public Tile placeTile(int tx, int ty, TileLayer layer, Tile tile, Object source) {
        return setTile(tx, ty, layer, tile);
    }
    
    public void joinWorld(Player player) {
        ecsEngine.addEntity(player.getPlayerEntity());
    }
    
    private static final ComponentMapper<ChunkMarkerComponent> CHUNK_COMP_MAPPER = ComponentMapper
            .getFor(ChunkMarkerComponent.class);
    private static final ComponentMapper<TransformComponent> TRANSFORM_COMP_MAPPER = ComponentMapper
            .getFor(TransformComponent.class);
    
    public void spawnEntity(Entity entity) {
        //TODO Check whether the entity would be colliding when spawned at this position
        //TODO what happens if the chunk is not loaded?
        if (TRANSFORM_COMP_MAPPER.has(entity)) {
            TransformComponent t = TRANSFORM_COMP_MAPPER.get(entity);
            int supposedChunkX = Chunk.toGlobalChunkf(t.position.x);
            int supposedChunkY = Chunk.toGlobalChunkf(t.position.y);
            Chunk c = this.chunkProvider.getChunk(supposedChunkX, supposedChunkY);
            c.addEntity(entity);
        }
        ecsEngine.addEntity(entity);
    }
    
    public void despawnEntity(Entity entity) {
        if (CHUNK_COMP_MAPPER.has(entity)) {
            Chunk c = CHUNK_COMP_MAPPER.get(entity).currentChunk;
            if (c != null) {
                c.removeEntity(entity);
            }
        } else if (TRANSFORM_COMP_MAPPER.has(entity)) {
            TransformComponent t = TRANSFORM_COMP_MAPPER.get(entity);
            int supposedChunkX = Chunk.toGlobalChunkf(t.position.x);
            int supposedChunkY = Chunk.toGlobalChunkf(t.position.y);
            Chunk c = this.chunkProvider.getChunk(supposedChunkX, supposedChunkY);
            c.removeEntity(entity);
        }
        ecsEngine.removeEntity(entity);
    }
    
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
    
    //Doesn't work for back layer tiles or non-solid tiles.
    public void raycast(IRaycastCallback callback, float x1, float y1, float x2, float y2) {
        PhysicsSystemBox2D physics = ecsEngine.getSystem(PhysicsSystemBox2D.class);//This is kinda spicy as the systems are handled in a World subclass...
        if (physics != null) {
            physics.raycast(callback, x1, y1, x2, y2);
        }
    }
    
    public long getSeed() {
        return seed;
    }
    
    public void unloadAll() {
        this.chunkProvider.queueUnloadAll();
        this.chunkProvider.unloadQueued();
        this.unchunkProvider.unload();
    }
}
