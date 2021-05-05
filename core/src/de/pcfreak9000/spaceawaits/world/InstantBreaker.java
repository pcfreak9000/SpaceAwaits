package de.pcfreak9000.spaceawaits.world;

import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class InstantBreaker implements ITileBreaker {
    
    public static final InstantBreaker INSTANCE = new InstantBreaker();
    
    private InstantBreaker() {
    }
    
    @Override
    public float getMaterialLevel() {
        return Float.POSITIVE_INFINITY;
    }
    
    @Override
    public float getSpeed() {
        return Float.POSITIVE_INFINITY;
    }
    
    @Override
    public void onBreak(int tx, int ty, TileLayer layer, Tile tile, World world) {
        //do nothing
    }
    
}
