package de.pcfreak9000.spaceawaits.world.tile;

import java.util.Random;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.Destructible;
import de.pcfreak9000.spaceawaits.world.World;

public interface IBreaker {
    
    public static final float FINISHED_BREAKING = 1f;
    public static final float ABORTED_BREAKING = -1f;
    
    float breakIt(World world, Destructible breakable, float progressCurrent);
    
    boolean canBreak(World world, Destructible breakable);
    
    void onBreak(World world, Destructible breakable, Array<ItemStack> drops, Random random);
    
}
