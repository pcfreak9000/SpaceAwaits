package de.pcfreak9000.spaceawaits.world.gen.biome;

import de.pcfreak9000.spaceawaits.content.gen.CaveSystem;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.gen.IChunkGenerator;
import de.pcfreak9000.spaceawaits.world.gen.RndHelper;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class BiomeChunkGenerator implements IChunkGenerator {
    
    private static final int POPULATE_DIV = 16;
    private static final int POPULATE_COUNT = Chunk.CHUNK_SIZE / POPULATE_DIV;
    
    private BiomeSystem biomeGen;
    private CaveSystem caveGen;
    
    private RndHelper rnd;
    private final long seed;
    
    public BiomeChunkGenerator(BiomeSystem biomeGenerator, CaveSystem caves, long seedMaster) {
        this.biomeGen = biomeGenerator;
        this.caveGen = caves;
        this.seed = seedMaster;
        this.rnd = new RndHelper();
    }
    
    @Override
    public void generateChunk(Chunk chunk) {
        rnd.set(seed, 1 + RndHelper.getSeedAt(seed, chunk.getGlobalChunkX(), chunk.getGlobalChunkY()));
        
        //m = source;
        //int[][] caves = Util.smoothCA(m, chunk.getGlobalTileX(), chunk.getGlobalTileY(), (Chunk.CHUNK_SIZE),
        //      (Chunk.CHUNK_SIZE), Direction.MOORE_NEIGHBOURS, 4, 20, -0.1);
        for (int i = 0; i < Chunk.CHUNK_SIZE; i++) {
            for (int j = 0; j < Chunk.CHUNK_SIZE; j++) {
                int x = i + chunk.getGlobalTileX();
                int y = j + chunk.getGlobalTileY();
                if (!chunk.inBounds(x, y)) {
                    continue;
                }
                Biome biome = biomeGen.getBiome(x, y);
                if (biome != null) {
                    boolean cave = caveGen == null ? false : caveGen.isCave(x, y);
                    Tile front = biome.genTileAt(x, y, TileLayer.Front, biomeGen, rnd);
                    Tile back = biome.genTileAt(x, y, TileLayer.Back, biomeGen, rnd);
                    if (!cave || !front.canBreak()) {
                        chunk.setTile(x, y, TileLayer.Front, front);
                    }
                    chunk.setTile(x, y, TileLayer.Back, back);
                }
            }
        }
    }
    
    @Override
    public void structureChunk(Chunk chunk, TileSystem ts) {
        rnd.set(seed, 2 + RndHelper.getSeedAt(seed, chunk.getGlobalChunkX(), chunk.getGlobalChunkY()));
        for (int i = 0; i < POPULATE_COUNT; i++) {
            for (int j = 0; j < POPULATE_COUNT; j++) {
                int txs = chunk.getGlobalTileX() + i * POPULATE_DIV;
                int tys = chunk.getGlobalTileY() + j * POPULATE_DIV;
                int sampletx = txs + POPULATE_DIV / 2;
                int samplety = tys + POPULATE_DIV / 2;
                if (!chunk.inBounds(sampletx, samplety)) {
                    continue;
                }
                Biome biome = biomeGen.getBiome(sampletx, samplety);
                if (biome != null)
                    biome.genStructureTiles(ts, biomeGen, txs, txs, POPULATE_DIV, rnd);
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
                Biome biome = biomeGen.getBiome(sampletx, samplety);
                if (biome != null)
                    biome.populate(ts, world, biomeGen, txs, tys, POPULATE_DIV, rnd);
            }
        }
    }
}
