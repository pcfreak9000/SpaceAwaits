package de.pcfreak9000.spaceawaits.generation;

import de.pcfreak9000.spaceawaits.world.WorldBounds;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;
import de.pcfreak9000.spaceawaits.world.gen.biome.BiomeChunkGenerator;

public class SpaceSurfaceGenerator implements IGeneratingLayer<WorldPrimer, SpaceSurfaceParams> {
    
    @Override
    public WorldPrimer generate(SpaceSurfaceParams params) {
        WorldPrimer p = new WorldPrimer();
        SpaceSurface surface = new SpaceSurface(null);
        p.setWorldBounds(new WorldBounds(params.getWidth(), params.getHeight()));
        p.setChunkGenerator(new BiomeChunkGenerator(surface, params.getSeed()));
        return p;
    }
    
}
