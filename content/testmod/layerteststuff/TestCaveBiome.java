package layerteststuff;

import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleFractal.FractalType;
import com.sudoplay.joise.module.SeededModule;

import de.pcfreak9000.spaceawaits.generation.ModuleRandom;
import de.pcfreak9000.spaceawaits.generation.NoiseGenerator;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.gen.CaveBiome;

public class TestCaveBiome extends CaveBiome {
    public TestCaveBiome() {
        iterations = 20;
        threshold = -0.1;
        noiseGenCreator = (seed) -> new NoiseGenerator(() -> genNoise(seed));
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
