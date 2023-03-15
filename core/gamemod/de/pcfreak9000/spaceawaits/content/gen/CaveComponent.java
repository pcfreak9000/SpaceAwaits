package de.pcfreak9000.spaceawaits.content.gen;

import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleFractal.FractalType;

import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.gen.biome.GenerationDataComponent;

public class CaveComponent implements GenerationDataComponent {
    
    private Module noise;
    
    public CaveComponent(long seed) {
        genNoise(seed);
    }
    
    public double getCaveNoise(int tx, int ty) {
        return noise.get(tx + 0.5, ty + 0.5);
    }
    
    public boolean isCave(int tx, int ty) {
        return getCaveNoise(tx, ty) > 0.55;
    }
    
    private void genNoise(long seed) {
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
        noise = source;
    }
    
}
