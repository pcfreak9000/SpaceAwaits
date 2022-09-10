package de.pcfreak9000.spaceawaits.generation;

import de.pcfreak9000.spaceawaits.world.gen.biome.Biome;
import de.pcfreak9000.spaceawaits.world.gen.biome.IBiomeGen;

public class SpaceSurface implements IBiomeGen {
    
    private BiomeGenExpander expander;
    
    //for attributes and stuff like height, maybe use a component system and search the parents for the right one?
    
    //does this need to be GenInfo?
    public SpaceSurface(GenInfo[] subgens) {
        this.expander = new BiomeGenExpander(subgens);
    }
    
    @Override
    public Biome getBiome(int tx, int ty) {
        return expander.getSubBiomeGen(0).getBiome(tx, ty);
    }
    
}
