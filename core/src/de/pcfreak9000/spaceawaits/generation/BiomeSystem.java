package de.pcfreak9000.spaceawaits.generation;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.world.gen.biome.Biome;
import de.pcfreak9000.spaceawaits.world.gen.biome.GenerationDataComponent;

public class BiomeSystem {
    
    private Array<Biome> availableBiomes;
    private FilterCollection<Biome> filterCol;
    
    private GenFilter2D<Biome> initialFilter;
    
    private Array<Biome> tmp = new Array<>();
    
    private Map<Class<?>, GenerationDataComponent> components = new HashMap<>();
    
    public BiomeSystem(GenFilter2D<Biome> initialFilter, Array<Biome> availableBiomes) {
        this.initialFilter = initialFilter;
        this.availableBiomes = availableBiomes;
        this.filterCol = new FilterCollection<>(this.availableBiomes);
    }
    
    public <T extends GenerationDataComponent> void setComponent(Class<T> key, T comp) {
        this.components.put(key, comp);
    }
    
    public <T extends GenerationDataComponent> T getComponent(Class<T> key) {
        return (T) components.get(key);
    }
    
    public Biome getBiome(int tx, int ty) {
        this.filterCol.reset();
        this.tmp.clear();
        this.initialFilter.filter(tx, ty, filterCol);
        this.filterCol.collectEnabled(tmp);
        return tmp.size == 0 ? null : tmp.get(0);
    }
    
}
