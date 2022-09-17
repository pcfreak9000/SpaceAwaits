package de.pcfreak9000.spaceawaits.world.gen.biome;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.gen.IChunkGenerator;
import de.pcfreak9000.spaceawaits.world.gen.RndHelper;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class BiomeChunkGenerator implements IChunkGenerator {
    
    private static final int POPULATE_DIV = 16;
    private static final int POPULATE_COUNT = Chunk.CHUNK_SIZE / POPULATE_DIV;
    
    private BiomeGenCompBased biomeGenerator;
    private RndHelper rnd;
    private final long seed;
    
    public BiomeChunkGenerator(BiomeGenCompBased biomeGenerator, long seedMaster) {
        this.biomeGenerator = biomeGenerator;
        this.seed = seedMaster;
        this.rnd = new RndHelper();
    }
    
    @Override
    public void generateChunk(Chunk chunk) {
        rnd.set(seed, 1 + RndHelper.getSeedAt(seed, chunk.getGlobalChunkX(), chunk.getGlobalChunkY()));
        for (int i = 0; i < Chunk.CHUNK_SIZE; i++) {
            for (int j = 0; j < Chunk.CHUNK_SIZE; j++) {
                int x = i + chunk.getGlobalTileX();
                int y = j + chunk.getGlobalTileY();
                if (!chunk.inBounds(x, y)) {
                    continue;
                }
                BiomeGenCompBased leaf = biomeGenerator.getLeaf(x, y);
                Biome biome = leaf.getBiome(x, y);
                biome.genTerrainTileAt(x, y, chunk, leaf, rnd);
            }
        }
    }
    
    @Override
    public void populateChunk(Chunk chunk, World world) {
        rnd.set(seed, 3 + RndHelper.getSeedAt(seed, chunk.getGlobalChunkX(), chunk.getGlobalChunkY()));
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
                BiomeGenCompBased leaf = biomeGenerator.getLeaf(sampletx, samplety);
                Biome biome = leaf.getBiome(sampletx, samplety);
                biome.populate(ts, world, leaf, txs, tys, POPULATE_DIV, rnd);
            }
        }
    }
}
