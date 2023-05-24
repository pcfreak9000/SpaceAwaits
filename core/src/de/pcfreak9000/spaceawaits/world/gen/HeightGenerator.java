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

import de.pcfreak9000.spaceawaits.generation.IGen1D;
import de.pcfreak9000.spaceawaits.generation.IGenInt1D;
import de.pcfreak9000.spaceawaits.generation.NoiseGenerator;
import de.pcfreak9000.spaceawaits.util.Util;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

public class HeightGenerator implements IGenInt1D {
    
    private int minheight, maxheight;
    
    private float offset;
    private float maxamplitude;
    
    private long seed;
    private Map<Class<? extends HeightBiome>, Data> noiseGens;
    
    private IGen1D<HeightBiome> heightBiomeGen;
    
    private Interpolation interpolinterpolation = Interpolation.linear;
    private int interpconstmax = 30;
    
    private static class Data {
        NoiseGenerator noiseGen;
        float amplitude;
        float offset;
    }
    
    public HeightGenerator(long seed, int minheight, int maxheight) {
        this.seed = seed;
        this.noiseGens = new HashMap<>();
        
        this.minheight = minheight;
        this.maxheight = maxheight;
        
        this.maxamplitude = (maxheight - minheight) / 2f;
        this.offset = minheight + maxamplitude;
        
        HeightBiome hb = new HeightBiome();
        hb.defaultAmplitude = maxamplitude;
        hb.noiseGenCreator = (seeds) -> new NoiseGenerator(() -> genNoise(seeds));
        
        this.heightBiomeGen = (x) -> hb;
    }
    
    @Override
    public int generate(int i) {
        return getHeight(i, 0);
    }
    
    public int getHeight(int tx, int ty) {
        int height = (int) Math.round(Util.interpolateStepwise(tx, heightBiomeGen, (atx, hb) -> {
            Data noiseGen = noiseGens.get(hb.getClass());
            if (noiseGen == null) {
                noiseGen = new Data();
                noiseGen.noiseGen = hb.getNoiseGenProvider().apply(seed);
                noiseGen.amplitude = hb.getAmplitude(maxamplitude);
                noiseGen.offset = hb.getOffset(maxamplitude, noiseGen.amplitude);
                noiseGens.put(hb.getClass(), noiseGen);
            }
            float amplitude = noiseGen.amplitude;
            float offset = noiseGen.offset;
            return (int) Math.round(this.offset + offset + amplitude * noiseGen.noiseGen.get().get(tx, 0.5));
        }, interpolinterpolation, interpconstmax));
        height = MathUtils.clamp(height, minheight, maxheight);
        return height;
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
