package de.pcfreak9000.spaceawaits.generation;

import de.pcfreak9000.spaceawaits.world.gen.biome.IBiomeGen;

@Deprecated
public abstract class BiomebasedLayer implements IBiomeGen {
    protected final BiomeGenExpander biomeExpander;
    
    public BiomebasedLayer(GenInfo[] layers) {
        this.biomeExpander = new BiomeGenExpander(layers);
    }
    
}
