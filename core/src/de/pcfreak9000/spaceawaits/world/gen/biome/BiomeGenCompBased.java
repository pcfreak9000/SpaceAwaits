package de.pcfreak9000.spaceawaits.world.gen.biome;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
@Deprecated
public abstract class BiomeGenCompBased implements IBiomeGen {
    
    private final BiomeGenCompBased parent;
    
    private ClassToInstanceMap<GenerationDataComponent> map;
    
    public BiomeGenCompBased(BiomeGenCompBased parent) {
        this.map = MutableClassToInstanceMap.create();
        this.parent = parent;
    }
    
    //return "this" if this is the desired leaf
    public abstract BiomeGenCompBased getLeaf(int tx, int ty);
    
    @Override
    public Biome getBiome(int tx, int ty) {
        BiomeGenCompBased leaf = getLeaf(tx, ty);
        if (leaf == this) {
            throw new IllegalStateException("A leaf must override getBiome!");
        }
        return leaf.getBiome(tx, ty);
    }
    
    //maybe change this and allow the Class to be a supertype of T
    protected <T extends GenerationDataComponent> void addComponent(Class<T> clazz, T comp) {
        map.put(clazz, comp);
    }
    
    public <T extends GenerationDataComponent> T getComponent(Class<T> clazz) {
        T data = map.getInstance(clazz);
        if (data == null && parent != null) {
            return parent.getComponent(clazz);
        }
        return data;
    }
    
    public boolean hasComponent(Class<? extends GenerationDataComponent> clazz) {
        return getComponent(clazz) != null;
    }
    
    public <T extends BiomeGenCompBased> T getParent(Class<T> clazz) {
        if (this.getClass().equals(clazz)) {
            return (T) this;
        }
        if (this.parent != null) {
            return this.parent.getParent(clazz);
        }
        return null;
    }
    
}
