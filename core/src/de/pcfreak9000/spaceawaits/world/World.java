package de.pcfreak9000.spaceawaits.world;

import java.util.Random;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.RandomXS128;

import de.omnikryptec.event.EventBus;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.core.ecs.ModifiedEngine;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.gen.IPlayerSpawn;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;
import de.pcfreak9000.spaceawaits.world.light.AmbientLightProvider;

public abstract class World {
    
    public static final float STEPLENGTH_SECONDS = 1 / 60f;
    
    private WorldBounds worldBounds;
    
    protected final IPlayerSpawn playerSpawn;
    protected final IWorldProperties worldProperties;
    private AmbientLightProvider ambientLightProvider;
    
    protected final Engine ecsEngine;
    protected final EventBus eventBus;
    
    //Used for random item drops etc, not terrain gen etc
    private final RandomXS128 worldRandom;
    
    private int countChunkActive = 0;
    
    public World(WorldPrimer primer) {
        //initialize fields
        this.ecsEngine = new ModifiedEngine(STEPLENGTH_SECONDS);
        this.eventBus = new EventBus();
        SpaceAwaits.BUS.register(eventBus);//Not too sure about this
        this.worldRandom = new RandomXS128();
        
        //do priming stuff
        this.worldBounds = primer.getWorldBounds();
        this.ambientLightProvider = primer.getLightProvider();
        this.playerSpawn = primer.getPlayerSpawn();
        this.worldProperties = primer.getWorldProperties();
        
    }
    
    public abstract void unloadWorld();
    
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
    
    public Random getWorldRandom() {
        return worldRandom;
    }
    
    public void setPlayer(Player player) {
        ecsEngine.addEntity(player.getPlayerEntity());
    }
    
    //Could keep the Player instance here and just removePlayer()...
    public void removePlayer(Player player) {
        ecsEngine.removeEntity(player.getPlayerEntity());
    }
    
    public <T extends EntitySystem> T getSystem(Class<T> clazz) {
        return ecsEngine.getSystem(clazz);
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
