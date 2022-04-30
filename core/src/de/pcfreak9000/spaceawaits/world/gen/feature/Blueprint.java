package de.pcfreak9000.spaceawaits.world.gen.feature;

import java.util.Random;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public interface Blueprint {
    
    void generate(TileSystem tiles, World world, int tx, int ty, int rx, int ry, int width, int height, Random random);
    
}
