package de.pcfreak9000.spaceawaits.world.gen.biome;

import com.badlogic.gdx.utils.Array;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

import de.pcfreak9000.spaceawaits.generation.FilterCollection;
import de.pcfreak9000.spaceawaits.generation.GenFilter2D;

public class BiomeSystem {
    
    private Array<Biome> availableBiomes;
    private FilterCollection<Biome> filterCol;
    
    private GenFilter2D<Biome> initialFilter;
    
    private Array<Biome> tmp = new Array<>();
    
    private ClassToInstanceMap<GenerationDataComponent> components;
    
    public BiomeSystem(GenFilter2D<Biome> initialFilter, Array<Biome> availableBiomes) {
        this.initialFilter = initialFilter;
        this.availableBiomes = availableBiomes;
        this.filterCol = new FilterCollection<>(this.availableBiomes);
        this.components = MutableClassToInstanceMap.create();
    }
    
    //maybe change this and allow the Class to be a supertype of T
    public <T extends GenerationDataComponent> void setComponent(Class<T> clazz, T comp) {
        this.components.put(clazz, comp);
    }
    
    public <T extends GenerationDataComponent> T getComponent(Class<T> clazz) {
        T data = this.components.getInstance(clazz);
        return data;
    }
    
    public boolean hasComponent(Class<? extends GenerationDataComponent> clazz) {
        return this.components.containsKey(clazz);
    }
    
    public <T extends GenFilter2D<Biome>> T getSubFilter(int tx, int ty, Class<T> clazz, String tag) {
        return this.initialFilter.getSubFilter2D(tx, ty, clazz, tag);
    }
    
    public Biome getBiome(int tx, int ty) {
        this.filterCol.reset();
        this.tmp.clear();
        this.initialFilter.filter(tx, ty, filterCol);
        this.filterCol.collectEnabled(tmp);
        return tmp.size == 0 ? null : tmp.get(0);
    }
    
}
