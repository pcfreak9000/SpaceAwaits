package de.pcfreak9000.spaceawaits.world.gen.biome;

import java.util.Random;

import com.badlogic.gdx.math.RandomXS128;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.gen.IChunkGenerator;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class BiomeChunkGenerator implements IChunkGenerator {
    
    private static final int POPULATE_DIV = 16;
    private static final int POPULATE_COUNT = Chunk.CHUNK_SIZE / POPULATE_DIV;
    
    private BiomeGenCompBased biomeGenerator;
    private long seed;
    
    private Random rand = new RandomXS128();
    
    public BiomeChunkGenerator(BiomeGenCompBased biomeGenerator, long seed) {
        this.biomeGenerator = biomeGenerator;
        this.seed = seed;
    }
    
    //TODO Move this? Not every world necessarily has a seed... but the random here does have a seed
    private long getSeedForChunk(Chunk chunk) {
        long l = seed;
        l += 6793451682347862416L;
        l *= chunk.getGlobalChunkX();
        l += 6793451682347862416L;
        l *= chunk.getGlobalChunkY();
        return l;
    }
    
    @Override
    public void generateChunk(Chunk chunk) {
        long chunkseed = getSeedForChunk(chunk);
        rand.setSeed(chunkseed);
        for (int i = 0; i < Chunk.CHUNK_SIZE; i++) {
            for (int j = 0; j < Chunk.CHUNK_SIZE; j++) {
                int x = i + chunk.getGlobalTileX();
                int y = j + chunk.getGlobalTileY();
                if (!chunk.inBounds(x, y)) {
                    continue;
                }
                Biome biome = biomeGenerator.getBiome(x, y);
                biome.setWorldSeedCurrent(this.seed);
                biome.setChunkSeedCurrent(chunkseed);
                biome.setRandomCurrent(rand);
                biome.genTerrainTileAt(x, y, chunk, this.biomeGenerator);
            }
        }
    }
    
    @Override
    public void populateChunk(Chunk chunk, World world) {
        long chunkseed = getSeedForChunk(chunk) + 768435;
        rand.setSeed(chunkseed);
        TileSystem ts = world.getSystem(TileSystem.class);
        for (int i = 0; i < POPULATE_COUNT; i++) {
            for (int j = 0; j < POPULATE_COUNT; j++) {
                int txs = chunk.getGlobalTileX() + i * POPULATE_DIV;
                int tys = chunk.getGlobalTileY() + j * POPULATE_DIV;
                int sampletx = txs + POPULATE_DIV / 2;
                int samplety = tys + POPULATE_DIV / 2;
                if (!chunk.inBounds(sampletx, samplety)) {
                    continue;
                }
                Biome biome = biomeGenerator.getBiome(sampletx, samplety);
                biome.setWorldSeedCurrent(this.seed);
                biome.setChunkSeedCurrent(chunkseed);
                biome.setRandomCurrent(rand);
                biome.populate(ts, world, biomeGenerator, txs, tys, POPULATE_DIV);
            }
        }
    }
}
