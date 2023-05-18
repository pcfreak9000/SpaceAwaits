package de.pcfreak9000.spaceawaits.generation;

import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleFractal.FractalType;

import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

public class HeightVariation implements IGenInt1D {
    
    private NoiseGenerator noiseGen;
    
    private int avgYpos;
    private int amplitude;
    
    private Module genNoise(long seed) {
        ModuleFractal gen = new ModuleFractal(FractalType.FBM, BasisType.SIMPLEX, InterpolationType.LINEAR);
        gen.setSeed(seed);
        gen.setNumOctaves(2);
        gen.setFrequency(0.00225);
        
        ModuleAutoCorrect source = new ModuleAutoCorrect(-1, 1);
        source.setSource(gen);
        source.setSampleScale(Chunk.CHUNK_SIZE * 2);
        source.setSamples(10000);
        source.calculate2D();
        return source;
    }
    
    public HeightVariation(int avgYpos, long seed, int amplitude) {
        this.avgYpos = avgYpos;
        this.amplitude = amplitude;
        noiseGen = new NoiseGenerator(() -> genNoise(seed));
    }
    
    public int getHeight(int i) {
        return avgYpos + (int) Math.round(amplitude * noiseGen.get().get(i, 0.5));
    }
    
    @Override
    public int generate(int i) {
        return getHeight(i);
    }
}
