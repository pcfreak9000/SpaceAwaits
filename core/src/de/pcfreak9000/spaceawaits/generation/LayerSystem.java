package de.pcfreak9000.spaceawaits.generation;

import de.pcfreak9000.spaceawaits.content.gen.LayerHeightVariation;

public class LayerSystem implements IGenInt1D {
    
    private int avgYpos;
    private LayerHeightVariation var;
    
    public LayerSystem(int avgYpos, LayerHeightVariation var) {
        this.avgYpos = avgYpos;
        this.var = var;
    }
    
    public int getHeight(int i) {
        return avgYpos + (var == null ? 0 : var.getVariation(i));
    }
    @Override
    public int generate(int i) {
        return getHeight(i);
    }
}
