package de.pcfreak9000.spaceawaits.world.gen.feature;

import java.util.Random;

import de.pcfreak9000.spaceawaits.world.WorldArea;
import de.pcfreak9000.spaceawaits.world.chunk.ITileArea;

public interface Blueprint {
    
    void generate(ITileArea tiles, WorldArea world, int tx, int ty, int rx, int ry, int width, int height, Random random);
    
}
