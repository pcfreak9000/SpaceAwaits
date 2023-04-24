package de.pcfreak9000.spaceawaits.world.gen.biome;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleFractal.FractalType;
import com.sudoplay.joise.module.SeededModule;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.generation.BiomeSystem;
import de.pcfreak9000.spaceawaits.util.Direction;
import de.pcfreak9000.spaceawaits.util.Util;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.gen.IChunkGenerator;
import de.pcfreak9000.spaceawaits.world.gen.ModuleRandom;
import de.pcfreak9000.spaceawaits.world.gen.RndHelper;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class BiomeChunkGenerator implements IChunkGenerator {
    
    private static final int POPULATE_DIV = 16;
    private static final int POPULATE_COUNT = Chunk.CHUNK_SIZE / POPULATE_DIV;
    
    private BiomeSystem biomeGen;
    private RndHelper rnd;
    private final long seed;
    
    public BiomeChunkGenerator(BiomeSystem biomeGenerator, long seedMaster) {
        this.biomeGen = biomeGenerator;
        this.seed = seedMaster;
        this.rnd = new RndHelper();
    }
    
    @Override
    public void generateChunk(Chunk chunk) {
        rnd.set(seed, 1 + RndHelper.getSeedAt(seed, chunk.getGlobalChunkX(), chunk.getGlobalChunkY()));
        SeededModule m = new ModuleRandom();
        m.setSeed(seed);
        ModuleFractal gen = new ModuleFractal(FractalType.FBM, BasisType.SIMPLEX, InterpolationType.LINEAR);
        gen.setSeed(seed);
        gen.setNumOctaves(3);
        gen.setFrequency(0.02);
        //        ModuleBasisFunction gen1 = new ModuleBasisFunction(BasisType.GRADIENT, InterpolationType.QUINTIC);
        //        gen1.setSeed(seed);
        //        
        //        ModuleScaleDomain gen = new ModuleScaleDomain();
        //        gen.setSource(gen1);
        //        gen.setScaleX(0.02);
        //        gen.setScaleY(0.02);
        
        ModuleAutoCorrect source = new ModuleAutoCorrect(-1, 1);
        source.setSource(gen);
        source.setSampleScale(Chunk.CHUNK_SIZE * 2);
        source.setSamples(10000);
        source.calculate2D();
        //m = source;
        int[][] caves = Util.smoothCA(m, chunk.getGlobalTileX(), chunk.getGlobalTileY(), (Chunk.CHUNK_SIZE),
                (Chunk.CHUNK_SIZE), Direction.MOORE_NEIGHBOURS, 4, 20, -0.1);
        for (int i = 0; i < Chunk.CHUNK_SIZE; i++) {
            for (int j = 0; j < Chunk.CHUNK_SIZE; j++) {
                int x = i + chunk.getGlobalTileX();
                int y = j + chunk.getGlobalTileY();
                if (!chunk.inBounds(x, y)) {
                    continue;
                }
                if (caves[i][j] == 0) {
                    // if (interpolate(i / 3f, j / 3f, caves, Interpolation.smooth) == 0) {
                    continue;
                }
                Biome biome = biomeGen.getBiome(x, y);
                if (biome != null) {
                    biome.genTerrainTileAt(x, y, chunk, biomeGen, rnd);
                }
            }
        }
    }
    
    public static int interpolate(float x, float y, int[][] array, Interpolation interpolator) {
        int x0 = Mathf.floori(x);
        int y0 = Mathf.floori(y);
        if (MathUtils.isEqual(x, x0) && MathUtils.isEqual(y, y0)) {
            return array[x0][y0];
        }
        int x1 = x0 + 1;
        int y1 = y0 + 1;
        float xs = interpolator.apply(x - x0);
        float ys = interpolator.apply(y - y0);
        return Math.round(interpolateXY2(x, y, xs, ys, x0, x1, y0, y1, array));
    }
    
    private static float interpolateX2(float x, float y, float xs, int x0, int x1, int iy, int[][] array) {
        float v1 = array[x0][iy];
        float v2 = array[x1][iy];
        return MathUtils.lerp(v1, v2, xs);
    }
    
    public static float interpolateXY2(float x, float y, float xs, float ys, int x0, int x1, int y0, int y1,
            int[][] array) {
        float v1 = interpolateX2(x, y, xs, x0, x1, y0, array);
        float v2 = interpolateX2(x, y, xs, x0, x1, y1, array);
        return MathUtils.lerp(v1, v2, ys);
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
                if(biome!=null)
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
                if(biome!=null)
                biome.populate(ts, world, biomeGen, txs, tys, POPULATE_DIV, rnd);
            }
        }
    }
}
