package de.pcfreak9000.spaceawaits.content.gen;

import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleFractal.FractalType;

import de.pcfreak9000.spaceawaits.generation.IGenInt1D;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.gen.biome.GenerationDataComponent;

public class HeightComponent implements GenerationDataComponent, IGenInt1D {
    
    private Module noise;
    
    private int offset;
    private double amplitude;
    
    public HeightComponent(long seed, int offset, double amplitude) {
        this.offset = offset;
        this.amplitude = amplitude;
        genNoise(seed);
    }
    
    @Override
    public int generate(int i) {
        return getHeight(i, 0);
    }
    
    public int getHeight(int tx, int ty) {
        return offset + (int) Math.round(amplitude * noise.get(tx, 0.5));
    }
    
    private void genNoise(long seed) {
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
        noise = source;
    }
    
}
