package de.pcfreak9000.spaceawaits.world2;

import de.pcfreak9000.spaceawaits.world.WorldBounds;
import de.pcfreak9000.spaceawaits.world.gen.ChunkGenerator;
import de.pcfreak9000.spaceawaits.world.light.AmbientLightProvider;

public class WorldPrimer {
    
    private ChunkGenerator chunkGenerator;
    private AmbientLightProvider lightProvider;
    private WorldBounds worldBounds;
    
    public ChunkGenerator getChunkGenerator() {
        return chunkGenerator;
    }
    
    public void setChunkGenerator(ChunkGenerator chunkGenerator) {
        this.chunkGenerator = chunkGenerator;
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
    
}
