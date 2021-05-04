package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Fixture;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.Player;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.ecs.entity.ChunkMarkerComponent;
import de.pcfreak9000.spaceawaits.world.light.AmbientLightProvider;
import de.pcfreak9000.spaceawaits.world.physics.IRaycastEntityCallback;
import de.pcfreak9000.spaceawaits.world.physics.IRaycastFixtureCallback;
import de.pcfreak9000.spaceawaits.world.physics.IRaycastTileCallback;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsSystemBox2D;
import de.pcfreak9000.spaceawaits.world.physics.UnitConversion;
import de.pcfreak9000.spaceawaits.world.physics.UserData;
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
    
    private final RaycastCallbackImpl raycastCallbackWr = new RaycastCallbackImpl();
    
    public void raycastEntities(IRaycastEntityCallback callback, float x1, float y1, float x2, float y2) {
        PhysicsSystemBox2D physics = ecsEngine.getSystem(PhysicsSystemBox2D.class);//This is kinda spicy as the systems are handled in a World subclass...
        if (physics != null) {
            raycastCallbackWr.callb = callback;
            physics.raycast(raycastCallbackWr, x1, y1, x2, y2);
            raycastCallbackWr.callb = null;
        }
    }
    
    public void raycastTiles(IRaycastTileCallback tileCallback, float x1, float y1, float x2, float y2,
            TileLayer layer) {
        /*
         * Based on the video Super Fast Ray Casting in Tiled Worlds using DDA by
         * javidx9 (2021, https://www.youtube.com/watch?v=NbSee-XM7WA).
         */
        //constants
        final int txStart = Tile.toGlobalTile(x1);
        final int tyStart = Tile.toGlobalTile(y1);
        final int txTarget = Tile.toGlobalTile(x2);
        final int tyTarget = Tile.toGlobalTile(y2);
        final float dx = x2 - x1;
        final float dy = y2 - y1;
        final float rayUnitStepSizeX = (float) Math.sqrt(1 + Mathf.square(dy / dx));
        final float rayUnitStepSizeY = (float) Math.sqrt(1 + Mathf.square(dx / dy));
        final int stepX = (int) Math.signum(dx);
        final int stepY = (int) Math.signum(dy);
        //prep loop vars
        float lenx, leny;
        if (dx < 0) {
            lenx = x1 - txStart;
        } else {
            lenx = txStart + 1 - x1;
        }
        if (dy < 0) {
            leny = y1 - tyStart;
        } else {
            leny = tyStart + 1 - y1;
        }
        lenx *= rayUnitStepSizeX;
        leny *= rayUnitStepSizeY;
        int tx = txStart;
        int ty = tyStart;
        for (int i = 0; i < Chunk.CHUNK_SIZE * 10; i++) {//while(true) is not so nice
            Tile t = getTile(tx, ty, layer);
            boolean continu = tileCallback.reportRayTile(t, tx, ty);
            if (!continu || (tx == txTarget && ty == tyTarget)) {
                break;
            }
            if (lenx < leny) {
                tx += stepX;
                lenx += rayUnitStepSizeX;
            } else {
                ty += stepY;
                leny += rayUnitStepSizeY;
            }
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
    
    private static class RaycastCallbackImpl implements IRaycastFixtureCallback {
        private IRaycastEntityCallback callb;
        private UserData ud = new UserData();
        
        @Override
        public float reportRayFixture(Fixture fixture, float pointx, float pointy, float normalx, float normaly,
                float fraction, UnitConversion conv) {
            //ignore everything which is not an Entity
            ud.set(fixture.getUserData());
            if (!ud.isEntity()) {
                return -1;
            }
            return callb.reportRayEntity(ud.getEntity(), pointx, pointy, normalx, normaly, fraction, conv);
        }
    }
}
