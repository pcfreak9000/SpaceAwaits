package de.pcfreak9000.spaceawaits.content.gen;

import de.pcfreak9000.spaceawaits.generation.BiomeGenExpander;
import de.pcfreak9000.spaceawaits.generation.GenInfo;
import de.pcfreak9000.spaceawaits.world.gen.biome.Biome;
import de.pcfreak9000.spaceawaits.world.gen.biome.BiomeGenCompBased;
import de.pcfreak9000.spaceawaits.world.gen.biome.IBiomeGen;

public class SpaceSurface extends BiomeGenCompBased {
    
    private IBiomeGen[] subs;
    
    public SpaceSurface() {
        super(null);
    }
    
    //does this need to be GenInfo?    
    public void expand(GenInfo[] subgens) {
        subs = BiomeGenExpander.expand(subgens);
    }
    
    @Override
    public Biome getBiome(int tx, int ty) {
        return subs[0].getBiome(tx, ty);
    }
    
}
