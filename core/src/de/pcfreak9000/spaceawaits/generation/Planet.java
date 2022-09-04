package de.pcfreak9000.spaceawaits.generation;

public class Planet implements IWorldGen {
    
    private GenInfo[] sublayers;
    
    private IWorldGen[] subgens;
    
    public Planet(GenInfo[] layers, PlanetParameters params, long seed) {
        this.sublayers = layers;
    }
    
    private void expand(GenInfo[] layers, long seed) {
        for (GenInfo i : layers) {
            i.generate(seed);
        }
    }
    
    @Override
    public GenInfo getFrom(int tx, int ty) {
        return sublayers[0];
    }
}
