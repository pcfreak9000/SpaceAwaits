package de.pcfreak9000.spaceawaits.world.tile;

import java.util.Random;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.Breakable;
import de.pcfreak9000.spaceawaits.world.World;

public interface IBreaker {
    
    float breakIt(World world, Breakable breakable, float progressCurrent);
    
    boolean canBreak(World world, Breakable breakable);
    
    void onBreak(World world, Breakable breakable, Array<ItemStack> drops, Random random);
    
}
