package de.pcfreak9000.spaceawaits.content.gen;

import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleFractal.FractalType;
import com.sudoplay.joise.module.SeededModule;

import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.gen.biome.GenerationDataComponent;

public class HeightComponent implements GenerationDataComponent {
    
    private long seedCurrent = SeededModule.DEFAULT_SEED;
    
    private Module noise;
    private SeededModule seeded;
    
    public HeightComponent() {
        genNoise();
    }
    
    public int getHeight(int tx, int ty, long seed) {
        checkSetSeed(seed);
        return 1000 + (int) Math.round(60 * noise.get(tx, 0.5));
    }
    
    private void genNoise() {
        ModuleFractal gen = new ModuleFractal(FractalType.FBM, BasisType.SIMPLEX, InterpolationType.LINEAR);
        gen.setNumOctaves(6);
        gen.setFrequency(0.00184);
        gen.setLacunarity(2.1);
        seeded = gen;
        
        ModuleAutoCorrect source = new ModuleAutoCorrect(-1, 1);
        source.setSource(gen);
        source.setSampleScale(Chunk.CHUNK_SIZE * 2);
        source.setSamples(10000);
        source.calculate2D();
        noise = source;
    }
    
    private void checkSetSeed(long seed) {
        if (seedCurrent != seed) {
            seeded.setSeed(seed);
            seedCurrent = seed;
        }
    }
}
