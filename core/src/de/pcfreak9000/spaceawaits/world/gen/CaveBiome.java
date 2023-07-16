package de.pcfreak9000.spaceawaits.world.gen;

import java.util.function.LongFunction;

import de.pcfreak9000.spaceawaits.generation.IGen2D;
import de.pcfreak9000.spaceawaits.generation.NoiseGenerator;
import de.pcfreak9000.spaceawaits.util.Direction;

public class CaveBiome implements IGen2D<CaveBiome> {
    protected Direction[] smoothRule = Direction.MOORE_NEIGHBOURS;
    protected int minSolidCount = 4;
    
    protected int iterations;
    protected double threshold;
    
    protected LongFunction<NoiseGenerator> noiseGenCreator;
    
    public LongFunction<NoiseGenerator> getNoiseGenCreator() {
        return noiseGenCreator;
    }
    
    public Direction[] getSmoothRule() {
        return smoothRule;
    }
    
    public int getMinSolidCount() {
        return minSolidCount;
    }
    
    public int getIterations() {
        return iterations;
    }
    
    public double getThreshold() {
        return threshold;
    }
    
    @Override
    public CaveBiome generate(int tx, int ty) {
        return this;
    }
}
