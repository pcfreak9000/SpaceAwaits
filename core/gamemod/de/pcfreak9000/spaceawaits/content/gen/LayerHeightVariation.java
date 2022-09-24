package de.pcfreak9000.spaceawaits.content.gen;

import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleFractal.FractalType;

import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

public class LayerHeightVariation {
    private Module noise;
    private int amplitude;
    
    public LayerHeightVariation(long seed, int amplitude) {
        this.amplitude = amplitude;
        genNoise(seed);
    }
    
    public int getVariation(int tx, int ty) {
        return (int) Math.round(amplitude * noise.get(tx, 0.5));
    }
    
    private void genNoise(long seed) {
        ModuleFractal gen = new ModuleFractal(FractalType.FBM, BasisType.SIMPLEX, InterpolationType.LINEAR);
        gen.setSeed(seed);
        gen.setNumOctaves(2);
        gen.setFrequency(0.00225);
        
        ModuleAutoCorrect source = new ModuleAutoCorrect(-1, 1);
        source.setSource(gen);
        source.setSampleScale(Chunk.CHUNK_SIZE * 2);
        source.setSamples(10000);
        source.calculate2D();
        noise = source;
    }
}
