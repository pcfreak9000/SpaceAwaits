package de.pcfreak9000.spaceawaits.world.tile;

import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.World;
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
    public void onTileBreak(int tx, int ty, TileLayer layer, Tile tile, World world, Array<ItemStack> drops,
            RandomXS128 random) {
        //do nothing
    }

    @Override
    public boolean canBreak(int tx, int ty, TileLayer layer, Tile tile, World world) {
        return true;
    }
    
}
