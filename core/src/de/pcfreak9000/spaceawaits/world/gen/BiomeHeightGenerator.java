package de.pcfreak9000.spaceawaits.world.gen;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

import de.pcfreak9000.spaceawaits.generation.IGen1D;
import de.pcfreak9000.spaceawaits.generation.IGenInt1D;
import de.pcfreak9000.spaceawaits.generation.NoiseGenerator;
import de.pcfreak9000.spaceawaits.util.Util;

public class BiomeHeightGenerator implements IGenInt1D {
    
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
    
    public BiomeHeightGenerator(long seed, int minheight, int maxheight, IGen1D<HeightBiome> heightBiomeGen) {
        this.seed = seed;
        this.noiseGens = new HashMap<>();
        
        this.minheight = minheight;
        this.maxheight = maxheight;
        
        this.maxamplitude = (maxheight - minheight) / 2f;
        this.offset = minheight + maxamplitude;
        
        this.heightBiomeGen = heightBiomeGen;
    }
    
    @Override
    public int generate(int i) {
        return getHeight(i);
    }
    
    public int getHeight(int tx) {
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
    
}
