package de.pcfreak9000.spaceawaits.generation;

import de.pcfreak9000.spaceawaits.world.gen.biome.Biome;

public class Layer implements IBiomeGen {
    
    private BiomeGenExpander expand;
    
    public Layer(GenInfo[] layers, LayerParameters params, long seed) {
        this.expand = new BiomeGenExpander(layers, seed);
    }
    
    @Override
    public Biome getFrom(int tx, int ty) {
        System.out.println("hello there");
        return null;
        //return expand.getSubBiomeGen(0).getFrom(tx, ty);
    }
}
