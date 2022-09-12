package de.pcfreak9000.spaceawaits.content.gen;

import de.pcfreak9000.spaceawaits.world.gen.biome.Biome;
import de.pcfreak9000.spaceawaits.world.gen.biome.BiomeGenCompBased;

public class Layer extends BiomeGenCompBased {
    
    private Biome b;
    private LayerParams params;
    
    public Layer(LayerParams params, Biome singleBiome) {
        super(params.getParent());
        this.params = params;
        this.b = singleBiome;
    }
    
    public LayerParams getParams() {
        return params;
    }
    
    @Override
    public Biome getBiome(int tx, int ty) {
        return b;
    }
    
    @Override
    public BiomeGenCompBased getLeaf(int tx, int ty) {
        return this;
    }
    
}
