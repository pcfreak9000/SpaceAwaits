package de.pcfreak9000.spaceawaits.world.breaking;

import java.util.Random;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;

public class InstantBreaker implements IBreaker {
    
    public static final InstantBreaker INSTANCE = new InstantBreaker();
    
    private InstantBreaker() {
    }
    
    @Override
    public float breakIt(Engine world, BreakableInfo breakable, float f) {
        return Float.POSITIVE_INFINITY;
    }
    
    @Override
    public boolean canBreak(Engine world, BreakableInfo breakable) {
        return true;
    }
    
    @Override
    public void onBreak(Engine world, BreakableInfo breakable, Array<ItemStack> drops, Random random) {
    }
    
}
