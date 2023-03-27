package de.pcfreak9000.spaceawaits.generation;

import de.pcfreak9000.spaceawaits.world.gen.biome.Biome;

public class GenFilter2DLayer extends GenFilter2D<Biome> {
    
    private LayerSystem layerSystem;
    private GenFilter2D<Biome> lowerBiomes;
    private GenFilter2D<Biome> higherBiomes;
    
    public GenFilter2DLayer(LayerSystem layerSystem, GenFilter2D<Biome> lowerBiomes, GenFilter2D<Biome> higherBiomes) {
        this.layerSystem = layerSystem;
    }
    
    public int getHeight(int tx) {
        return layerSystem.getHeight(tx);
    }
    
    private boolean isLower(int tx, int ty) {
        return ty <= getHeight(tx);
    }
    
    @Override
    public void filter(int tx, int ty, FilterCollection<Biome> stuff) {
        boolean lower = isLower(tx, ty);
        for (int i = 0; i < stuff.size(); i++) {
            if (!lower) {
                stuff.disable(i);
            }
            //TODO filtering based on certain tags
        }
        GenFilter2D<Biome> child = selectChild(tx, ty);
        if (child != null) {
            child.filter(tx, ty, stuff);
        }
    }
    
    @Override
    protected GenFilter2D<Biome> selectChild(int tx, int ty) {
        return isLower(tx, ty) ? lowerBiomes : higherBiomes;
    }
    
}
