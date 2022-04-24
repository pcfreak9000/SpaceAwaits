package de.pcfreak9000.spaceawaits.world;

import java.util.Random;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import de.omnikryptec.event.EventBus;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.WorldEvents.WorldMetaNBTEvent.Type;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkMarkerComponent;
import de.pcfreak9000.spaceawaits.world.ecs.ModifiedEngine;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.world.gen.IPlayerSpawn;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;
import de.pcfreak9000.spaceawaits.world.light.AmbientLightProvider;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public abstract class World {
    
    public static final float STEPLENGTH_SECONDS = 1 / 60f;
    
    private WorldBounds worldBounds;
    private final long seed;
    
    protected final IChunkProvider chunkProvider;
    protected final IChunkLoader chunkLoader;
    protected final IUnchunkProvider unchunkProvider;
    protected final IPlayerSpawn playerSpawn;
    protected final IWorldProperties worldProperties;
    private AmbientLightProvider ambientLightProvider;
    
    protected final Engine ecsEngine;
    protected final EventBus eventBus;
    
    //Used for random item drops etc, not terrain gen etc
    protected final RandomXS128 worldRandom;
    
    //    public long time;//TMP until there is a proper class updating the universe
    //    private float timehelper;
    
    private int countChunkActive = 0;
    
    public World(WorldPrimer primer, long seed) {
        //initialize fields
        this.seed = seed;
        this.ecsEngine = new ModifiedEngine(STEPLENGTH_SECONDS);
        this.eventBus = new EventBus();
        SpaceAwaits.BUS.register(eventBus);//Not too sure about this
        this.worldRandom = new RandomXS128(seed);
        
        //do priming stuff
        this.worldBounds = primer.getWorldBounds();
        this.ambientLightProvider = primer.getLightProvider();
        this.playerSpawn = primer.getPlayerSpawn();
        this.worldProperties = primer.getWorldProperties();
        
        this.chunkLoader = createChunkLoader(primer);
        this.unchunkProvider = createUnchunkProvider(primer);
        this.chunkProvider = createChunkProvider(primer);
        //        //setup
        //        finishSetup(primer, ecsEngine);
    }
    
    //    protected abstract void finishSetup(WorldPrimer primer, Engine ecs);
    
    protected abstract IChunkProvider createChunkProvider(WorldPrimer primer);
    
    protected abstract IUnchunkProvider createUnchunkProvider(WorldPrimer primer);
    
    protected abstract IChunkLoader createChunkLoader(WorldPrimer primer);
    
    public void update(float dt) {
        this.ecsEngine.update(dt);
        //this.chunkProvider.unloadQueued();
        
        //        timehelper += dt * 50;
        //        if (timehelper >= 1) {
        //            int i = Mathf.floori(timehelper);
        //            timehelper -= i;
        //            time += i;
        //        }
    }
    
    protected void addChunk(Chunk c) {
        c.addToECS(ecsEngine);
        countChunkActive++;
    }
    
    protected void removeChunk(Chunk c) {
        c.removeFromECS();
        countChunkActive--;
    }
    
    public long getSeedForTile(int tx, int ty) {
        long l = getSeed();
        l += 8793457682347863416L;
        l *= tx;
        l += 8793457682347863416L;
        l *= ty;
        return l;
    }
    
    public Random getRandomForTile(int tx, int ty) {
        return new RandomXS128(getSeedForTile(tx, ty));
    }
    
    public void joinWorld(Player player) {
        ecsEngine.addEntity(player.getPlayerEntity());
    }
    
    public boolean spawnEntity(Entity entity, boolean checkOccupation) {
        //what happens if the chunk is not loaded? -> the chunk gets loaded if this World has the generating backend, but spawning should only happen there anyways
        //TODO what happens if the coordinates are somewhere out of bounds?
        if (Components.TRANSFORM.has(entity) && Components.PHYSICS.has(entity) && checkOccupation) {
            TransformComponent t = Components.TRANSFORM.get(entity);
            PhysicsComponent pc = Components.PHYSICS.get(entity);
            Vector2 wh = pc.factory.boundingBoxWidthAndHeight();
            //getSystem... oof
            if (ecsEngine.getSystem(TileSystem.class).checkSolidOccupation(t.position.x + wh.x / 4,
                    t.position.y + wh.y / 4, wh.x / 2, wh.y / 2)) {
                return false;
            }
        }
        if (Components.TRANSFORM.has(entity) && !Components.GLOBAL_MARKER.has(entity)) {
            TransformComponent t = Components.TRANSFORM.get(entity);
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
        //        DynamicAssetUtil.checkAndCreateAsset(entity);
        return true;
    }
    
    public void despawnEntity(Entity entity) {
        if (Components.CHUNK_MARKER.has(entity)) {
            Chunk c = Components.CHUNK_MARKER.get(entity).currentChunk;
            if (c != null) {
                c.removeEntity(entity);
            }
        } else if (Components.TRANSFORM.has(entity) && !Components.GLOBAL_MARKER.has(entity)) {
            TransformComponent t = Components.TRANSFORM.get(entity);
            int supposedChunkX = Chunk.toGlobalChunkf(t.position.x);
            int supposedChunkY = Chunk.toGlobalChunkf(t.position.y);
            Chunk c = this.chunkProvider.getChunk(supposedChunkX, supposedChunkY);
            c.removeEntity(entity);
        }
        unchunkProvider.get().removeEntity(entity);
        ecsEngine.removeEntity(entity);
        //        DynamicAssetUtil.checkAndDisposeAsset(entity);
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
    
    public void unloadAll() {
        this.getWorldBus().post(new WorldEvents.WorldMetaNBTEvent(this.unchunkProvider.worldInfo(), Type.Writing));
        ((ChunkProvider) chunkProvider).saveAll();
        ((ChunkProvider) chunkProvider).releaseAll();
        this.unchunkProvider.unload();
        ecsEngine.removeAllEntities();
        EntitySystem[] syss = ecsEngine.getSystems().toArray(EntitySystem.class);
        for (EntitySystem es : syss) {
            ecsEngine.removeSystem(es);
            SpaceAwaits.BUS.unregister(es);//Hmmm... the systems should handle this or even better, just use the worlds EventBus
            if (es instanceof Disposable) {
                Disposable d = (Disposable) es;
                d.dispose();
            }
        }
        SpaceAwaits.BUS.unregister(eventBus);
    }
    
    public int getLoadedChunksCount() {
        return chunkProvider.getLoadedChunkCount();
    }
    
    public int getUpdatingChunksCount() {
        return countChunkActive;
    }
    
    public WorldBounds getBounds() {
        return worldBounds;
    }
    
    public AmbientLightProvider getLightProvider() {
        return ambientLightProvider;
    }
    
    public IPlayerSpawn getPlayerSpawn() {
        return this.playerSpawn;
    }
    
    public long getSeed() {
        return seed;
    }
    
    public EventBus getWorldBus() {
        return this.eventBus;
    }
    
    @Deprecated
    public Random getWorldRandom() {
        return worldRandom;
    }
    
    public <T extends EntitySystem> T getSystem(Class<T> clazz) {
        return ecsEngine.getSystem(clazz);
    }
    
    public IWorldProperties getWorldProperties() {
        return worldProperties;
    }
}
