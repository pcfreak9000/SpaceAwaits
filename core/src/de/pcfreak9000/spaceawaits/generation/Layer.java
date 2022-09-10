package de.pcfreak9000.spaceawaits.generation;

import de.pcfreak9000.spaceawaits.world.gen.biome.Biome;
import de.pcfreak9000.spaceawaits.world.gen.biome.IBiomeGen;

@Deprecated
public class Layer implements IBiomeGen {
    
    private BiomeGenExpander expand;
    
    public Layer(GenInfo[] layers, LayerParameters params) {
        this.expand = new BiomeGenExpander(layers);
    }
    
    @Override
    public Biome getBiome(int tx, int ty) {
        System.out.println("hello there");
        return null;
        //return expand.getSubBiomeGen(0).getFrom(tx, ty);
    }
}
