package de.pcfreak9000.spaceawaits.generation;

import de.pcfreak9000.spaceawaits.world.gen.biome.Biome;

public class Generation {
    private GenLayerTestPlanet toplayer = new GenLayerTestPlanet();
    
    public void setup(long seed) {
        GenInfo[] planetlayers = toplayer.generate(seed, new PlanetParameters());
        GenInfo[] heightbiomes = planetlayers[0].generate(seed);
    }
    
    public void gen(long seed) {
        Planet me = toplayer.generateS(seed, new PlanetParameters());
        me.getFrom(0, 0);
    }
    
    public Biome getBiome(int tx, int ty) {
        //Interpolate stuff
        int atx = tx;
        int aty = ty;
        
        return null;
    }
}
