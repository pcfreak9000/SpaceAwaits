package de.pcfreak9000.spaceawaits.world.gen;

import de.pcfreak9000.spaceawaits.world.GlobalGenerator;
import de.pcfreak9000.spaceawaits.world.WorldMeta;

/**
 * The output of a {@link WorldGenerator}
 *
 */
public class WorldGenerationBundle {
    
    private long seed;
    
    private WorldMeta meta;
    
    private ChunkGenerator generator;
    
    private GlobalGenerator globalgen;
    
    public WorldGenerationBundle(long seed, WorldMeta meta, ChunkGenerator generator, GlobalGenerator globalgen) {
        this.meta = meta;
        this.generator = generator;
        this.globalgen = globalgen;
    }
    
    public WorldMeta getMeta() {
        return meta;
    }
    
    public GlobalGenerator getGlobalGenerator() {
        return globalgen;
    }
    
    public ChunkGenerator getChunkGenerator() {
        return generator;
    }
    
    public long getSeed() {
        return seed;
    }
    
}
