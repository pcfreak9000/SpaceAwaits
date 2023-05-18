package de.pcfreak9000.spaceawaits.world.gen;

import de.pcfreak9000.spaceawaits.generation.GenerationDataComponent;
import de.pcfreak9000.spaceawaits.generation.IGenInt1D;

public class ShapeSystem implements GenerationDataComponent {
    
    private IGenInt1D height;
    
    public ShapeSystem(IGenInt1D height) {
        this.height = height;
    }
    
    @Deprecated
    public int getHeight(int tx) {
        return height.generate(tx);
    }
    
    public float getNextSurfaceDistance(int tx, int ty) {
        return getHeight(tx) - ty;
    }
    
    public boolean hasTile(int tx, int ty) {
        return ty <= getHeight(tx);
    }
}