package de.pcfreak9000.spaceawaits.world.gen;

import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleFractal.FractalType;
import com.sudoplay.joise.module.SeededModule;

import de.pcfreak9000.spaceawaits.generation.GenerationDataComponent;
import de.pcfreak9000.spaceawaits.generation.ModuleRandom;
import de.pcfreak9000.spaceawaits.generation.NoiseGenerator;
import de.pcfreak9000.spaceawaits.util.Direction;
import de.pcfreak9000.spaceawaits.util.IntCoordKey;
import de.pcfreak9000.spaceawaits.util.SpecialCache;
import de.pcfreak9000.spaceawaits.util.Util;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

public class CaveSystem implements GenerationDataComponent {
    
    private NoiseGenerator noiseGen;
    private SpecialCache<IntCoordKey, int[][]> scache;
    
    public CaveSystem(long seed) {
        this.noiseGen = new NoiseGenerator(() -> genNoise(seed));
        this.scache = new SpecialCache<>(60, 55, (key)->{
            return Util.smoothCA(noiseGen, key.getX() * Chunk.CHUNK_SIZE, key.getY() * Chunk.CHUNK_SIZE,
                    Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE, Direction.MOORE_NEIGHBOURS, 4, 20, -0.1);
        }, null);
    }
    
    public boolean isCave(int tx, int ty) {
        IntCoordKey key = new IntCoordKey(Chunk.toGlobalChunk(tx), Chunk.toGlobalChunk(ty));
        int[][] ints = scache.getOrFresh(key);
        return ints[tx - key.getX() * Chunk.CHUNK_SIZE][ty - key.getY() * Chunk.CHUNK_SIZE] == 0;
    }
    
    private Module genNoise(long seed) {
        //        ModuleFractal gen = new ModuleFractal(FractalType.FBM, BasisType.SIMPLEX, InterpolationType.LINEAR);
        //        gen.setSeed(seed);
        //        gen.setNumOctaves(3);
        //        gen.setFrequency(0.02);
        ////        ModuleBasisFunction gen1 = new ModuleBasisFunction(BasisType.GRADIENT, InterpolationType.QUINTIC);
        ////        gen1.setSeed(seed);
        ////        
        ////        ModuleScaleDomain gen = new ModuleScaleDomain();
        ////        gen.setSource(gen1);
        ////        gen.setScaleX(0.02);
        ////        gen.setScaleY(0.02);
        //
        //        ModuleAutoCorrect source = new ModuleAutoCorrect(-1, 1);
        //        source.setSource(gen);
        //        source.setSampleScale(Chunk.CHUNK_SIZE * 2);
        //        source.setSamples(10000);
        //        source.calculate2D();
        //        noise = source;
        //        
        
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
        return m;
    }
    
}
