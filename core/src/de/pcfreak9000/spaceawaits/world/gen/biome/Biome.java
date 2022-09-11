package de.pcfreak9000.spaceawaits.world.gen.biome;

import java.util.Random;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.ITileArea;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public abstract class Biome {
    
    //???????
    //gen Biome
    //  Caves 
    //decorate Biome
    //  Ores, Plants, etc
    //  Structures
    
    private long worldseedCurrent;
    private long chunkseedCurrent;
    private Random randomCurrent;
    
    void setWorldSeedCurrent(long l) {
        this.worldseedCurrent = l;
    }
    
    void setChunkSeedCurrent(long l) {
        this.chunkseedCurrent = l;
    }
    
    void setRandomCurrent(Random r) {
        this.randomCurrent = r;
    }
    
    protected long getWorldSeed() {
        return worldseedCurrent;
    }
    
    protected long getChunkSeed() {
        return chunkseedCurrent;
    }
    
    protected Random getRandom() {
        return randomCurrent;
    }
    
    public abstract void genTerrainTileAt(int tx, int ty, ITileArea tiles, BiomeGenCompBased biomeGen);
    
    public abstract void populate(TileSystem tiles, World world, BiomeGenCompBased biomeGen, int tx, int ty,
            int populateDiv);
}
