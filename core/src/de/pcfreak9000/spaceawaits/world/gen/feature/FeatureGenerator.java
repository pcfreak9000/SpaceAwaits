package de.pcfreak9000.spaceawaits.world.gen.feature;

import java.util.Random;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public interface FeatureGenerator {
    
    boolean generate(TileSystem tiles, World world, int tx, int ty, Random rand, int area);
    
}
