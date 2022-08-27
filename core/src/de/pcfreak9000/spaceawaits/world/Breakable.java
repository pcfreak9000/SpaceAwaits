package de.pcfreak9000.spaceawaits.world;

import java.util.Random;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public interface Breakable {
    
    boolean canBreak();//TODO ??????
    
    float getHardness();
    
    float getMaterialLevel();
    
    void onBreak(World world, int tx, int ty, TileLayer layer, Array<ItemStack> drops, Random random);
    
}
