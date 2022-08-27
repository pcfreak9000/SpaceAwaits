package de.pcfreak9000.spaceawaits.world.tile;

import java.util.Random;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.Destructible;
import de.pcfreak9000.spaceawaits.world.World;

public class InstantBreaker implements IBreaker {
    
    public static final InstantBreaker INSTANCE = new InstantBreaker();
    
    private InstantBreaker() {
    }
    
    @Override
    public float breakIt(World world, Destructible breakable, float f) {
        return Float.POSITIVE_INFINITY;
    }
    
    @Override
    public boolean canBreak(World world, Destructible breakable) {
        return true;
    }
    
    @Override
    public void onBreak(World world, Destructible breakable, Array<ItemStack> drops, Random random) {
    }
    
}
