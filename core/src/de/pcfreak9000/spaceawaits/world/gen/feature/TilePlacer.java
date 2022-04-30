package de.pcfreak9000.spaceawaits.world.gen.feature;

import java.util.Random;

import de.pcfreak9000.spaceawaits.world.chunk.ITileArea;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public interface TilePlacer {
    
    void place(int tx, int ty, TileLayer layer, Random random, ITileArea tiles);
}
