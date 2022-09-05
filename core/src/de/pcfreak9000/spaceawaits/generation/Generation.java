package de.pcfreak9000.spaceawaits.generation;

import de.pcfreak9000.spaceawaits.world.gen.biome.Biome;

public class Generation {
    private static GenLayerTestPlanet toplayer = new GenLayerTestPlanet();
    
    public static void setup(long seed) {
        Planet p = toplayer.generate(seed, new PlanetParameters());
        p.getFrom(0, 0);
    }
    
    public Biome getBiome(int tx, int ty) {
        //Interpolate stuff
        int atx = tx;
        int aty = ty;
        
        return null;
    }
}
