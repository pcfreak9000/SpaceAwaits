package de.pcfreak9000.spaceawaits.world.tile;

import java.util.Random;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.Breakable;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public interface ITileBreaker {
    
    float breakIt(World world, Breakable breakable, int tx, int ty, TileLayer layer, float progressCurrent);
    
    boolean canBreak(World world, Breakable breakable, int tx, int ty, TileLayer layer);
    
    void onBreak(World world, Breakable breakable, int tx, int ty, TileLayer layer, Array<ItemStack> drops,
            Random random);
    
}
