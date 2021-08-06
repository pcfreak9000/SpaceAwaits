package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongMap;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.core.Player;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.util.IntCoords;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkMarkerComponent;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.TickChunkSystem;
import de.pcfreak9000.spaceawaits.world.ecs.ItemStackComponent;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.gen.IPlayerSpawn;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;
import de.pcfreak9000.spaceawaits.world.light.AmbientLightProvider;
import de.pcfreak9000.spaceawaits.world.physics.IQueryCallback;
import de.pcfreak9000.spaceawaits.world.physics.IRaycastEntityCallback;
import de.pcfreak9000.spaceawaits.world.physics.IRaycastFixtureCallback;
import de.pcfreak9000.spaceawaits.world.physics.IRaycastTileCallback;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsSystemBox2D;
import de.pcfreak9000.spaceawaits.world.physics.UnitConversion;
import de.pcfreak9000.spaceawaits.world.physics.UserDataHelper;
import de.pcfreak9000.spaceawaits.world.tile.BreakTileProgress;
import de.pcfreak9000.spaceawaits.world.tile.ITileBreaker;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.TileEntity;

public abstract class World {
    
    private WorldBounds worldBounds;
    private final long seed;
    
    protected final IChunkProvider chunkProvider;
    protected final IUnchunkProvider unchunkProvider;
    protected final IPlayerSpawn playerSpawn;
    protected final IWorldProperties worldProperties;
    private AmbientLightProvider ambientLightProvider;
    
    protected final Engine ecsEngine;
    protected final LongMap<BreakTileProgress> breakingTiles = new LongMap<>();
    
    //Used for random item drops etc, not terrain gen etc
    protected final RandomXS128 worldRandom;
    
    public World(WorldPrimer primer, long seed) {
        //initialize fields
        this.seed = seed;
        this.ecsEngine = new Engine();
        this.worldRandom = new RandomXS128(seed);
        
        //do priming stuff
        this.worldBounds = primer.getWorldBounds();
        this.ambientLightProvider = primer.getLightProvider();
        this.playerSpawn = primer.getPlayerSpawn();
        this.worldProperties = primer.getWorldProperties();
        
        this.unchunkProvider = createUnchunkProvider(primer);
        this.chunkProvider = createChunkProvider(primer);
        //        //setup
        //        finishSetup(primer, ecsEngine);
    }
    
    //    protected abstract void finishSetup(WorldPrimer primer, Engine ecs);
    
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
    
    private final EntityOccupationChecker entCheck = new EntityOccupationChecker();
    
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
                breakingTiles.remove(IntCoords.toLong(tx, ty));
                old.onTileRemoved(tx, ty, layer, this);
                tile.onTileSet(tx, ty, layer, this);
                notifyNeighbours(tile, old, tx, ty, layer);
                return old;
            }
        }
        return null;
    }
    
    //Hmmm. What about tiles on the edge to only loaded but not updated? What about resonance cascades?
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
        return Tile.NOTHING;
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
    
    public Tile placeTile(int tx, int ty, TileLayer layer, Tile tile) {
        if (layer == TileLayer.Back) {//if back layer, check for front layer? basically a gameplay decision, not sure
            Tile front = getTile(tx, ty, TileLayer.Front);
            if (front.isSolid() || front.isOpaque()) {
                return null;
            }
        }
        Tile current = getTile(tx, ty, layer);
        if (current != null && !current.canBeReplaced()) {
            //check current occupation, only place tile if there isnt already one
            return null;
        }
        if (tile.isSolid()) {
            if (entCheck(tx, ty, tx + 0.99f, ty + 0.99f)) {
                return null;
            }
        }
        Tile ret = setTile(tx, ty, layer, tile);
        tile.onTilePlaced(tx, ty, layer, this);
        return ret;
    }
    
    public float breakTile(int tx, int ty, TileLayer layer, ITileBreaker breaker) {
        //TODO allow null tilebreaker?
        //First check if this is allowed
        if (layer == TileLayer.Back) {
            Tile front = getTile(tx, ty, TileLayer.Front);
            if (front.isSolid()) {
                return -1f;
            }
        }
        Tile tile = getTile(tx, ty, layer);
        if (!tile.canBreak() && !breaker.ignoreTileCanBreak()) {
            return -1f;
        }
        if (tile.getMaterialLevel() > breaker.getMaterialLevel()
                && breaker.getMaterialLevel() != Float.POSITIVE_INFINITY) {
            return -1f;
        }
        if (!breaker.canBreak(tx, ty, layer, tile, this)) {
            return -1f;
        }
        long l = IntCoords.toLong(tx, ty);
        BreakTileProgress t = breakingTiles.get(l);
        if (t == null || t.getLayer() != layer) {
            t = new BreakTileProgress(tx, ty, layer);
            breakingTiles.put(l, t);
        }
        float speedActual = breaker.getSpeed() / tile.getHardness();
        t.incProgress(speedActual * Gdx.graphics.getDeltaTime());//Hmmmm oof
        if (t.getProgress() >= 1f) {
            Array<ItemStack> drops = new Array<>();
            setTile(tx, ty, layer, this.worldProperties.getTileOnBreak(tx, ty, layer));
            tile.onTileBroken(tx, ty, layer, drops, this, worldRandom);
            breaker.onTileBreak(tx, ty, layer, tile, this, drops, worldRandom);
            if (drops.size > 0) {
                for (ItemStack s : drops) {
                    dropItemStack(s, tx + worldRandom.nextFloat() / 2f - Item.WORLD_SIZE / 2,
                            ty + worldRandom.nextFloat() / 2F - Item.WORLD_SIZE / 2);
                }
                drops.clear();
            }
            return 1f;
        }
        return Mathf.clamp(t.getProgress(), 0, 1f);
    }
    
    public void joinWorld(Player player) {
        ecsEngine.addEntity(player.getPlayerEntity());
    }
    
    private static final ComponentMapper<ChunkMarkerComponent> CHUNK_COMP_MAPPER = ComponentMapper
            .getFor(ChunkMarkerComponent.class);
    private static final ComponentMapper<TransformComponent> TRANSFORM_COMP_MAPPER = ComponentMapper
            .getFor(TransformComponent.class);
    private static final ComponentMapper<ItemStackComponent> ITEMSTACK_COMP_MAPPER = ComponentMapper
            .getFor(ItemStackComponent.class);
    private static final ComponentMapper<PhysicsComponent> PHYSICS_COMP_MAPPER = ComponentMapper
            .getFor(PhysicsComponent.class);
    
    public void dropItemStack(ItemStack stack, float x, float y) {
        Entity e = CoreRes.ITEM_FACTORY.createEntity();
        ITEMSTACK_COMP_MAPPER.get(e).stack = stack;
        TRANSFORM_COMP_MAPPER.get(e).position.set(x, y);
        spawnEntity(e, false);
    }
    
    public boolean spawnEntity(Entity entity, boolean checkOccupation) {
        //TODO what happens if the chunk is not loaded? -> theoretically could use ProbeChunkManager, but this is World and not necessarily WorldCombined... maybe change the ChunkProvider stuff?
        //TODO what happens if the coordinates are somewhere out of bounds?
        //in both cases c is null and false is returned, but...
        if (TRANSFORM_COMP_MAPPER.has(entity)) {
            TransformComponent t = TRANSFORM_COMP_MAPPER.get(entity);
            if (PHYSICS_COMP_MAPPER.has(entity) && checkOccupation) {
                PhysicsComponent pc = PHYSICS_COMP_MAPPER.get(entity);
                Vector2 wh = pc.factory.boundingBoxWidthAndHeight();
                if (checkSolidOccupation(t.position.x + wh.x / 4, t.position.y + wh.y / 4, wh.x / 2, wh.y / 2)) {
                    return false;
                }
            }
            int supposedChunkX = Chunk.toGlobalChunkf(t.position.x);
            int supposedChunkY = Chunk.toGlobalChunkf(t.position.y);
            Chunk c = this.chunkProvider.getChunk(supposedChunkX, supposedChunkY);
            if (c == null) {
                return false;//Not so nice, this way the entity is just forgotten 
            }
            c.addEntity(entity);
            if (c.isActive()) {
                ecsEngine.addEntity(entity);
            }
        } else {
            //Hmmm...
            unchunkProvider.get().addEntity(entity);
            ecsEngine.addEntity(entity);
        }
        return true;
    }
    
    public boolean checkSolidOccupation(float x, float y, float w, float h) {
        if (entCheck(x, y, x + w, y + h)) {
            return true;
        }
        int ix = Mathf.floori(x);
        int iy = Mathf.floori(y);
        int iw = Mathf.ceili(x + w);
        int ih = Mathf.ceili(y + h);
        for (int i = ix; i < iw; i++) {
            for (int j = iy; j < ih; j++) {
                Tile t = getTile(i, j, TileLayer.Front);
                if (t.isSolid()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean entCheck(float x1, float y1, float x2, float y2) {
        queryAABB(entCheck, x1, y1, x2, y2);
        boolean b = entCheck.ud.isEntity();
        entCheck.ud.clear();
        return b;
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
        unchunkProvider.get().removeEntity(entity);
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
    
    public void queryAABB(IQueryCallback callback, float x1, float y1, float x2, float y2) {
        PhysicsSystemBox2D physics = ecsEngine.getSystem(PhysicsSystemBox2D.class);//This is kinda spicy as the systems are handled in a World subclass...
        if (physics != null) {
            physics.queryAABB(callback, x1, y1, x2, y2);
        }
    }
    
    public void raycastTiles(IRaycastTileCallback tileCallback, float x1, float y1, float x2, float y2,
            TileLayer layer) {
        /*
         * Based on the video "Super Fast Ray Casting in Tiled Worlds using DDA" by
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
    
    public int getLoadedChunksCount() {
        return this.chunkProvider.loadedChunkCount();
    }
    
    public int getUpdatingChunksCount() {
        return this.ecsEngine.getSystem(TickChunkSystem.class).getEntities().size();//Ooof, this is pretty specific...
    }
    
    private static final class RaycastCallbackImpl implements IRaycastFixtureCallback {
        private IRaycastEntityCallback callb;
        private final UserDataHelper ud = new UserDataHelper();
        
        @Override
        public float reportRayFixture(Fixture fixture, float pointx, float pointy, float normalx, float normaly,
                float fraction, UnitConversion conv) {
            //ignore everything which is not an Entity
            ud.set(fixture.getUserData(), fixture);
            if (!ud.isEntity()) {
                return -1;
            }
            return callb.reportRayEntity(ud.getEntity(), pointx, pointy, normalx, normaly, fraction, conv);
        }
    }
    
    private static final class EntityOccupationChecker implements IQueryCallback {
        
        public final UserDataHelper ud = new UserDataHelper();
        
        @Override
        public boolean reportFixture(Fixture fix, UnitConversion conv) {
            if (fix.isSensor()) {
                return true;
            }
            ud.set(fix.getUserData(), fix);
            return !ud.isEntity();
        }
        
    }
    
}
