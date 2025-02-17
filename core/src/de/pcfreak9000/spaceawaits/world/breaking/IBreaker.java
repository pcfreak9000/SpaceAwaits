package de.pcfreak9000.spaceawaits.world.breaking;

import java.util.Random;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;

public interface IBreaker {
    
    public static final float FINISHED_BREAKING = 1f;
    public static final float ABORTED_BREAKING = -1f;
    
    float breakIt(Engine world, BreakableInfo breakable, float progressCurrent);
    
    boolean canBreak(Engine world, BreakableInfo breakable);
    
    void onBreak(Engine world, BreakableInfo breakable, Array<ItemStack> drops, Random random);
    
}
