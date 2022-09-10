package de.pcfreak9000.spaceawaits.world.gen;

import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.spaceawaits.world.IWorldProperties;
import de.pcfreak9000.spaceawaits.world.WorldBounds;
import de.pcfreak9000.spaceawaits.world.light.AmbientLightProvider;

public class WorldPrimer {
    
    private IChunkGenerator iChunkGenerator;
    private AmbientLightProvider lightProvider;
    private IPlayerSpawn playerSpawn;
    private IWorldProperties worldProperties;
    private IWorldGenerator worldGenerator;
    private WorldBounds worldBounds;
    
    public WorldPrimer() {
        this.lightProvider = AmbientLightProvider.constant(Color.WHITE);
        this.worldProperties = IWorldProperties.defaultProperties();
    }
    
    public IWorldGenerator getWorldGenerator() {
        return worldGenerator;
    }
    
    public void setWorldGenerator(IWorldGenerator worldGenerator) {
        this.worldGenerator = worldGenerator;
    }
    
    public IChunkGenerator getChunkGenerator() {
        return iChunkGenerator;
    }
    
    public void setChunkGenerator(IChunkGenerator iChunkGenerator) {
        this.iChunkGenerator = iChunkGenerator;
    }
    
    public AmbientLightProvider getLightProvider() {
        return lightProvider;
    }
    
    public void setLightProvider(AmbientLightProvider lightProvider) {
        this.lightProvider = lightProvider;
    }
    
    public WorldBounds getWorldBounds() {
        return worldBounds;
    }
    
    public void setWorldBounds(WorldBounds worldBounds) {
        this.worldBounds = worldBounds;
    }
    
    public IPlayerSpawn getPlayerSpawn() {
        return this.playerSpawn;//TODO somehow generate a default from world bounds?
    }
    
    public void setPlayerSpawn(IPlayerSpawn playerSpawn) {
        this.playerSpawn = playerSpawn;
    }
    
    public IWorldProperties getWorldProperties() {
        return worldProperties;
    }
    
    public void setWorldProperties(IWorldProperties worldProperties) {
        this.worldProperties = worldProperties;
    }
    
}
