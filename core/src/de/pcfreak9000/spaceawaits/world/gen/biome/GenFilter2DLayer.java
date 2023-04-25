package de.pcfreak9000.spaceawaits.world.gen.biome;

import de.pcfreak9000.spaceawaits.generation.FilterCollection;
import de.pcfreak9000.spaceawaits.generation.GenFilter2D;
import de.pcfreak9000.spaceawaits.generation.IGenInt1D;

public class GenFilter2DLayer extends GenFilter2D<Biome> {
    
    private IGenInt1D layerSystem;
    private GenFilter2D<Biome> lowerBiomes;
    private GenFilter2D<Biome> higherBiomes;
    
    private Object dislikeHigherIndicator;
    private Object dislikeLowerIndicator;
    
    public GenFilter2DLayer(IGenInt1D layerSystem, GenFilter2D<Biome> lowerBiomes, GenFilter2D<Biome> higherBiomes,
            Object dislikeHigherIndicator, Object dislikeLowerIndicator) {
        this.layerSystem = layerSystem;
        this.lowerBiomes = lowerBiomes;
        this.higherBiomes = higherBiomes;
        this.dislikeHigherIndicator = dislikeHigherIndicator;
        this.dislikeLowerIndicator = dislikeLowerIndicator;
        if (this.dislikeHigherIndicator == null && this.dislikeLowerIndicator == null) {
            throw new NullPointerException();
        }
    }
    
    public int getHeight(int tx) {
        return layerSystem.generate(tx);
    }
    
    private boolean isLower(int tx, int ty) {
        return ty <= getHeight(tx);
    }
    
    @Override
    protected void filterFlat(int tx, int ty, FilterCollection<Biome> stuff) {
        boolean lower = isLower(tx, ty);
        for (int i = 0; i < stuff.size(); i++) {
            if (lower && (dislikeLowerIndicator == null || stuff.get(i).hasTag(dislikeLowerIndicator))) {
                stuff.disable(i);
            } else if (!lower && (dislikeHigherIndicator == null || stuff.get(i).hasTag(dislikeHigherIndicator))) {
                stuff.disable(i);
            }
        }
    }
    
    @Override
    protected GenFilter2D<Biome> selectChild(int tx, int ty) {
        return isLower(tx, ty) ? lowerBiomes : higherBiomes;
    }
    
}
