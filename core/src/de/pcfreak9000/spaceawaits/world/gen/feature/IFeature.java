package de.pcfreak9000.spaceawaits.world.gen.feature;

import java.util.Random;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.ITileArea;

public interface IFeature {
    boolean generate(World world, ITileArea tiles, int tx, int ty, Random rand);
    
}
