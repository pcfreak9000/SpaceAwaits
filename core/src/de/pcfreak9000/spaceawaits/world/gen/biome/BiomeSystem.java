package de.pcfreak9000.spaceawaits.world.gen.biome;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.generation.FilterCollection;
import de.pcfreak9000.spaceawaits.generation.GenFilter2D;
import de.pcfreak9000.spaceawaits.generation.IGen2D;

public class BiomeSystem implements IGen2D<Biome> {
    
    private Array<Biome> availableBiomes;
    private FilterCollection<Biome> filterCol;
    
    private GenFilter2D<Biome> initialFilter;
    
    private Array<Biome> tmp = new Array<>();
    
    public BiomeSystem(GenFilter2D<Biome> initialFilter, Array<Biome> availableBiomes) {
        this.initialFilter = initialFilter;
        this.availableBiomes = availableBiomes;
        this.filterCol = new FilterCollection<>(this.availableBiomes);
    }
    
    public <T extends GenFilter2D<Biome>> T getSubFilter(int tx, int ty, Class<T> clazz, String tag) {
        return this.initialFilter.getSubFilter2D(tx, ty, clazz, tag);
    }
    
    @Deprecated
    public Biome getBiome(int tx, int ty) {
        this.filterCol.reset();
        this.tmp.clear();
        this.initialFilter.filter(tx, ty, filterCol);
        this.filterCol.collectEnabled(tmp);
        return tmp.size == 0 ? null : tmp.get(0);
    }
    
    @Override
    public Biome generate(int x, int ty) {
        return getBiome(x, ty);
    }
    
}
