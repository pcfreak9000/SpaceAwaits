package de.pcfreak9000.spaceawaits.generation;

import de.pcfreak9000.spaceawaits.world.gen.biome.Biome;

public class Planet implements IBiomeGen {
    
    private BiomeGenExpander expand;
    
    public Planet(GenInfo[] layers, PlanetParameters params, long seed) {
        this.expand = new BiomeGenExpander(layers, seed);
    }
    
    @Override
    public Biome getFrom(int tx, int ty) {
        return expand.getSubBiomeGen(0).getFrom(tx, ty);
    }
}
