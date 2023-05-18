package de.pcfreak9000.spaceawaits.world.gen;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleFractal.FractalType;

import de.pcfreak9000.spaceawaits.generation.IGenInt1D;
import de.pcfreak9000.spaceawaits.generation.NoiseGenerator;
import de.pcfreak9000.spaceawaits.util.IStepwise1D;
import de.pcfreak9000.spaceawaits.util.Util;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

public class HeightGenerator implements IGenInt1D {
    
    private int minheight, maxheight;
    
    private long seed;
    private Map<Class<? extends HeightBiome>, NoiseGenerator[]> noiseGenCache;
    
    private IStepwise1D<HeightBiome> heightBiomeSelector;
    
    public HeightGenerator(long seed, int offset, double amplitude) {
        this.seed = seed;
        this.minheight = offset - (int) amplitude;
        this.maxheight = offset + (int) amplitude;
        this.noiseGenCache = new HashMap<>();
        HeightBiome hb = new HeightBiome() {
            
            @Override
            public int getInterpolationDistance() {
                return 10;
            }
            
            @Override
            public Interpolation getInterpolation() {
                return Interpolation.linear;
            }
            
            @Override
            public int getHeight(int x, int minHeight, int maxHeight, long seed, NoiseGenerator[] noises) {
                return offset + (int) Math.round(amplitude * noises[0].get().get(x, 0.5));
            }
            
            @Override
            public NoiseGenerator[] createNoiseGenerators(long seed) {
                return new NoiseGenerator[] { new NoiseGenerator(() -> genNoise(seed)) };
            }
        };
        this.heightBiomeSelector = (x) -> hb;
    }
    
    @Override
    public int generate(int i) {
        return getHeight(i, 0);
    }
    
    public int getHeight(int tx, int ty) {
        int height = (int) Math.round(Util.interpolateStepwise(tx, heightBiomeSelector, (atx, hb) -> {
            NoiseGenerator[] noiseGens = noiseGenCache.get(hb.getClass());
            if (noiseGens == null) {
                noiseGens = hb.createNoiseGenerators(seed);
                noiseGenCache.put(hb.getClass(), noiseGens);
            }
            return hb.getHeight(atx, minheight, maxheight, seed, noiseGens);
        }, Interpolation.linear, 30));
        height = MathUtils.clamp(height, minheight, maxheight);
        return height;//TODO Proper queued cache would be nice here
        //return offset + (int) Math.round(amplitude * noise.get().get(tx, 0.5));
    }
    
    private Module genNoise(long seed) {
        ModuleFractal gen = new ModuleFractal(FractalType.FBM, BasisType.SIMPLEX, InterpolationType.LINEAR);
        gen.setSeed(seed);
        gen.setNumOctaves(6);
        gen.setFrequency(0.00184);
        gen.setLacunarity(2.1);
        
        ModuleAutoCorrect source = new ModuleAutoCorrect(-1, 1);
        source.setSource(gen);
        source.setSampleScale(Chunk.CHUNK_SIZE * 2);
        source.setSamples(10000);
        source.calculate2D();
        return source;
    }
    
}
