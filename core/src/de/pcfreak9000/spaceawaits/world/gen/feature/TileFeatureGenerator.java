package de.pcfreak9000.spaceawaits.world.gen.feature;

import java.util.Random;

import de.pcfreak9000.spaceawaits.world.chunk.ITileArea;

public interface TileFeatureGenerator {
    boolean generate(ITileArea tiles, int tx, int ty, Random rand, int area);
    
}
