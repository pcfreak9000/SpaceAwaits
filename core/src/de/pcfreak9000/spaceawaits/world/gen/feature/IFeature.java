package de.pcfreak9000.spaceawaits.world.gen.feature;

import java.util.Random;

import de.pcfreak9000.spaceawaits.world.ITileArea;
import de.pcfreak9000.spaceawaits.world.WorldArea;

public interface IFeature {
    boolean generate(WorldArea world, ITileArea tiles, int tx, int ty, Random rand);//TODO tilelayer
    
}
