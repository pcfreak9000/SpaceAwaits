package de.pcfreak9000.spaceawaits.world.tile;

import java.util.Random;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.Destructible;

public class InstantBreaker implements IBreaker {
    
    public static final InstantBreaker INSTANCE = new InstantBreaker();
    
    private InstantBreaker() {
    }
    
    @Override
    public float breakIt(Engine world, Destructible breakable, float f) {
        return Float.POSITIVE_INFINITY;
    }
    
    @Override
    public boolean canBreak(Engine world, Destructible breakable) {
        return true;
    }
    
    @Override
    public void onBreak(Engine world, Destructible breakable, Array<ItemStack> drops, Random random) {
    }
    
}
