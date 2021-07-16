package de.pcfreak9000.spaceawaits.world;

import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.spaceawaits.world.gen.IChunkGenerator;
import de.pcfreak9000.spaceawaits.world.gen.IPlayerSpawn;
import de.pcfreak9000.spaceawaits.world.gen.IUnchunkGenerator;
import de.pcfreak9000.spaceawaits.world.light.AmbientLightProvider;

public class WorldPrimer {
    
    private IChunkGenerator iChunkGenerator;
    private IUnchunkGenerator unchunkGenerator;
    private AmbientLightProvider lightProvider;
    private IPlayerSpawn playerSpawn;
    private WorldBounds worldBounds;
    
    public WorldPrimer() {
        this.lightProvider = AmbientLightProvider.constant(Color.WHITE);
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
    
    public IUnchunkGenerator getUnchunkGenerator() {
        return unchunkGenerator;
    }
    
    public void setUnchunkGenerator(IUnchunkGenerator unchunkGenerator) {
        this.unchunkGenerator = unchunkGenerator;
    }
    
    public IPlayerSpawn getPlayerSpawn() {
        return this.playerSpawn;
    }
    
    public void setPlayerSpawn(IPlayerSpawn playerSpawn) {
        this.playerSpawn = playerSpawn;
    }
    
}
