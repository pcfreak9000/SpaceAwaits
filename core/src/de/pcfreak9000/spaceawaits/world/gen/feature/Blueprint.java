package de.pcfreak9000.spaceawaits.world.gen.feature;

import java.util.Random;

import de.pcfreak9000.spaceawaits.world.ITileArea;
import de.pcfreak9000.spaceawaits.world.WorldArea;

public interface Blueprint {
    
    void generate(ITileArea tiles, WorldArea world, int tx, int ty, int rx, int ry, int width, int height, Random random);
    
}
