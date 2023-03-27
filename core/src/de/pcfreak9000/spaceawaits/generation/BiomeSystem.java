package de.pcfreak9000.spaceawaits.generation;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.world.gen.biome.Biome;

public class BiomeSystem {
    
    private Array<Biome> availableBiomes = new Array<>();
    private FilterCollection<Biome> filterCol = new FilterCollection<>(availableBiomes);
    
    private GenFilter2D<Biome> initialFilter;
    
    private Array<Biome> tmp = new Array<>();
    
    public BiomeSystem(GenFilter2D<Biome> initialFilter) {
        this.initialFilter = initialFilter;
    }
    
    public void registerBiome(Biome b) {
        this.availableBiomes.add(b);
    }
    
    public Biome getBiome(int tx, int ty) {
        this.filterCol.reset();
        this.initialFilter.filter(tx, ty, filterCol);
        filterCol.collectEnabled(tmp);
        return tmp.get(0);
    }
    
}
