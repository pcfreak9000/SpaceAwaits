package de.pcfreak9000.spaceawaits.world;

import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public interface ITileBreaker {
    
    float getMaterialLevel();
    
    float getSpeed();
    
    default boolean ignoreCanBreak() {
        return false;
    }
    
    void onBreak(int tx, int ty, TileLayer layer, Tile tile, World world);
}
