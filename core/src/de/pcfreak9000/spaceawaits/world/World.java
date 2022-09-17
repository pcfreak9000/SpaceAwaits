package de.pcfreak9000.spaceawaits.world;

import java.util.Random;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Disposable;

import de.omnikryptec.event.EventBus;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.WorldEvents.WorldMetaNBTEvent.Type;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.ecs.ModifiedEngine;
import de.pcfreak9000.spaceawaits.world.gen.IPlayerSpawn;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;
import de.pcfreak9000.spaceawaits.world.light.AmbientLightProvider;

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
    }
    
    protected abstract IChunkProvider createChunkProvider(WorldPrimer primer);
    
    protected abstract IUnchunkProvider createUnchunkProvider(WorldPrimer primer);
    
    protected abstract IChunkLoader createChunkLoader(WorldPrimer primer);
    
    public void update(float dt) {
        this.ecsEngine.update(dt);
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
    
    @Deprecated
    public Random getWorldRandom() {
        return worldRandom;
    }
    
    public Random createRandomForTile(int tx, int ty) {
        return new RandomXS128(getSeedForTile(tx, ty));
    }
    
    public long getSeed() {
        return seed;
    }
    
    public void joinWorld(Player player) {
        ecsEngine.addEntity(player.getPlayerEntity());
    }
    
    public void unloadAll() {
        this.getWorldBus().post(new WorldEvents.WorldMetaNBTEvent(this.unchunkProvider.worldInfo(), Type.Writing));
        ((ChunkProvider) chunkProvider).saveAll();
        ((ChunkProvider) chunkProvider).unloadAll();
        this.unchunkProvider.unload();
        ecsEngine.removeAllEntities();
        EntitySystem[] syss = ecsEngine.getSystems().toArray(EntitySystem.class);
        for (EntitySystem es : syss) {
            ecsEngine.removeSystem(es);
            SpaceAwaits.BUS.unregister(es);//Forcefully unregister systems which would otherwise be dangling 
            if (es instanceof Disposable) {
                Disposable d = (Disposable) es;
                d.dispose();
            }
        }
        SpaceAwaits.BUS.unregister(eventBus);
    }
    
    public <T extends EntitySystem> T getSystem(Class<T> clazz) {
        return ecsEngine.getSystem(clazz);
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
    
    public EventBus getWorldBus() {
        return this.eventBus;
    }
    
    public IWorldProperties getWorldProperties() {
        return worldProperties;
    }
}
