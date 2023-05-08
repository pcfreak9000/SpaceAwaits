package de.pcfreak9000.spaceawaits.world.gen;

import de.pcfreak9000.spaceawaits.generation.NoiseGenerator;
import de.pcfreak9000.spaceawaits.util.IStepWiseComponent;

public interface HeightBiome extends IStepWiseComponent {
    
    int getHeight(int x, int minHeight, int maxHeight, long seed, NoiseGenerator[] noises);
    
    NoiseGenerator[] createNoiseGenerators(long seed);
}
