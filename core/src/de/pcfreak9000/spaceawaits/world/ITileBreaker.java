package de.pcfreak9000.spaceawaits.world;

import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public interface ITileBreaker {
    
    //TODO possibly move this into canBreak
    float getMaterialLevel();
    
    float getSpeed();
    
    default boolean ignoreTileCanBreak() {
        return false;
    }
    
    boolean canBreak(int tx, int ty, TileLayer layer, Tile tile, World world);
    
    void onTileBreak(int tx, int ty, TileLayer layer, Tile tile, World world, Array<ItemStack> drops, RandomXS128 random);
}
