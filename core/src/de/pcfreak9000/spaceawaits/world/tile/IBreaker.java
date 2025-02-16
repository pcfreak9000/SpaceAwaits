package de.pcfreak9000.spaceawaits.world.tile;

import java.util.Random;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.Destructible;

public interface IBreaker {
    
    public static final float FINISHED_BREAKING = 1f;
    public static final float ABORTED_BREAKING = -1f;
    
    float breakIt(Engine world, Destructible breakable, float progressCurrent);
    
    boolean canBreak(Engine world, Destructible breakable);
    
    void onBreak(Engine world, Destructible breakable, Array<ItemStack> drops, Random random);
    
}
