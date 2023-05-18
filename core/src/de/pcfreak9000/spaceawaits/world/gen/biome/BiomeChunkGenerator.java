package de.pcfreak9000.spaceawaits.world.gen.biome;

import com.badlogic.gdx.utils.IntArray;

import de.pcfreak9000.spaceawaits.generation.GenerationParameters;
import de.pcfreak9000.spaceawaits.generation.IGen2D;
import de.pcfreak9000.spaceawaits.generation.RndHelper;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.gen.CaveSystem;
import de.pcfreak9000.spaceawaits.world.gen.IChunkGenerator;
import de.pcfreak9000.spaceawaits.world.gen.ShapeSystem;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class BiomeChunkGenerator implements IChunkGenerator {
    
    private static final int POPULATE_DIV = 16;
    private static final int POPULATE_COUNT = Chunk.CHUNK_SIZE / POPULATE_DIV;
    
    private ShapeSystem shapeSystem;
    private IGen2D<Biome> biomeGen;
    private CaveSystem caveGen;
    
    private GenerationParameters genParams;
    
    private RndHelper rnd;
    private final long seed;
    
    public BiomeChunkGenerator(ShapeSystem shapeSystem, IGen2D<Biome> biomeGenerator, CaveSystem caves, long seedMaster,
            GenerationParameters params) {
        this.shapeSystem = shapeSystem;
        this.biomeGen = biomeGenerator;
        this.caveGen = caves;
        this.seed = seedMaster;
        this.rnd = new RndHelper();
        this.genParams = params;
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
                if (!this.shapeSystem.hasTile(x, y)) {
                    continue;
                }
                Biome biome = biomeGen.generate(x, y);//Allow null biomes? Maybe for the vacuum of space on an asteroid?
                if (biome != null) {
                    Tile tile;
                    if (shapeSystem.getNextSurfaceDistance(x, y) == 0) {
                        tile = biome.getTopTile();
                    } else {
                        tile = biome.getTile();
                    }
                    boolean cave = caveGen == null ? false : caveGen.isCave(x, y);
                    //Tile front = biome.genTileAt(x, y, TileLayer.Front, biomeGen, rnd);
                    //Tile back = biome.genTileAt(x, y, TileLayer.Back, biomeGen, rnd);
                    if (!cave || !tile.canBreak()) {
                        chunk.setTile(x, y, TileLayer.Front, tile);
                    }
                    chunk.setTile(x, y, TileLayer.Back, tile);
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
                Biome biome = biomeGen.generate(sampletx, samplety);
                if (biome != null)
                    biome.genStructureTiles(ts, genParams, txs, txs, POPULATE_DIV, rnd);
            }
        }
    }
    
    @Override
    public void populateChunk(Chunk chunk, World world) {
        rnd.set(seed, 3 + RndHelper.getSeedAt(seed, chunk.getGlobalChunkX(), chunk.getGlobalChunkY()));
        TileSystem ts = world.getSystem(TileSystem.class);
        for (int j = 0; j < POPULATE_COUNT; j++) {
            for (int i = 0; i < POPULATE_COUNT; i++) {
                int txs = chunk.getGlobalTileX() + i * POPULATE_DIV;
                int tys = chunk.getGlobalTileY() + j * POPULATE_DIV;
                int sampletx = txs + POPULATE_DIV / 2;
                int samplety = tys + POPULATE_DIV / 2;
                if (!chunk.inBounds(sampletx, samplety)) {
                    continue;
                }
                Biome biome = biomeGen.generate(sampletx, samplety);
                if (biome != null)
                    biome.populate(ts, world, genParams, txs, tys, POPULATE_DIV, rnd);
                if (biome != null && biome.getDeco() != null) {
                    biome.getDeco().decorate(ts, world, genParams, sampletx, samplety, POPULATE_DIV, rnd);
                }
               
            }
        }
        IntArray iarr = new IntArray(Chunk.CHUNK_SIZE);
        for (int i = 0; i < Chunk.CHUNK_SIZE; i++) {
            int height = shapeSystem.getHeight(i + chunk.getGlobalTileX());
            if (!chunk.inBounds(chunk.getGlobalTileX() + i, height)) {
                continue;
            }
            iarr.add(i);
        }
        int length = 0;
        int begin = 0;
        for (int index = 0; index < iarr.size; index++) {
            if (index < iarr.size - 1 && iarr.get(index) + 1 == iarr.get(index + 1) && length < POPULATE_DIV) {
                length++;
            } else {
                int sampletx = begin + (length / 2);
                int samplety = shapeSystem.getHeight(sampletx);
                Biome biome = biomeGen.generate(sampletx, samplety);
                if (biome != null && biome.getSurfaceDeco() != null) {
                    biome.getSurfaceDeco().decorate(ts, world, genParams, begin + chunk.getGlobalTileX(), length, rnd);
                }
                begin = index < iarr.size - 1 ? iarr.get(index + 1) : -1;
                length = 0;
            }
        }
    }
}
