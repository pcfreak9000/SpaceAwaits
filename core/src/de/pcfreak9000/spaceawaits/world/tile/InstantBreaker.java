package de.pcfreak9000.spaceawaits.world.tile;

import java.util.Random;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.Breakable;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class InstantBreaker implements ITileBreaker {
    
    public static final InstantBreaker INSTANCE = new InstantBreaker();
    
    private InstantBreaker() {
    }
    
    @Override
    public float breakIt(World world, Breakable breakable, int tx, int ty, TileLayer layer, float f) {
        return Float.POSITIVE_INFINITY;
    }
    
    @Override
    public boolean canBreak(World world, Breakable breakable, int tx, int ty, TileLayer layer) {
        return true;
    }
    
    @Override
    public void onBreak(World world, Breakable breakable, int tx, int ty, TileLayer layer, Array<ItemStack> drops,
            Random random) {
    }
    
}
