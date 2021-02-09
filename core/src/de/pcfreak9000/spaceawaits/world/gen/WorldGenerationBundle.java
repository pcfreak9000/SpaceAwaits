package de.pcfreak9000.spaceawaits.world.gen;

import de.pcfreak9000.spaceawaits.world.Background;
import de.pcfreak9000.spaceawaits.world.WorldMeta;
import de.pcfreak9000.spaceawaits.world.light.AmbientLightProvider;

/**
 * The output of a {@link WorldGenerator}
 *
 */
public class WorldGenerationBundle {
    
    private long seed;
    
    private WorldMeta meta;
    
    private Background back;//TMP?
    
    private AmbientLightProvider ambient;
    
    private ChunkGenerator generator;
    
    public WorldGenerationBundle(long seed, WorldMeta meta, Background back, AmbientLightProvider ambient,
            ChunkGenerator generator) {
        this.meta = meta;
        this.back = back;
        this.ambient = ambient;
        this.generator = generator;
    }
    
    public WorldMeta getMeta() {
        return meta;
    }
    
    public Background getBackground() {
        return back;
    }
    
    public AmbientLightProvider getAmbientLight() {
        return ambient;
    }
    
    public ChunkGenerator getChunkGenerator() {
        return generator;
    }
    
    public long getSeed() {
        return seed;
    }
    
}
