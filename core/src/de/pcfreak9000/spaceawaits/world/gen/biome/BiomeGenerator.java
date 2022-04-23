package de.pcfreak9000.spaceawaits.world.gen.biome;

public abstract class BiomeGenerator {
    
    protected final long worldSeed;
    
    public BiomeGenerator(long seed) {
        this.worldSeed = seed;
    }
    
    public abstract Biome getBiome(int tx, int ty);
    
    public long getWorldSeed() {
        return this.worldSeed;
    }
    
    public float interpolateAlongX(Class<? extends BiomeInterpolatable> interpolator, int tx, int ty) {
        Biome biome = getBiome(tx, ty);
        if (!biome.hasInterpolatable(interpolator)) {
            throw new IllegalArgumentException(
                    "Biome at x=" + tx + ", y=" + ty + " doesn't have the requested BiomeInterpolatable");
        }
        //find "overlapping" biomes here and do the interpolation
        return biome.getInterpolatable(interpolator).get(tx, ty);
    }
}
