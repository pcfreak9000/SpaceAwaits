package de.pcfreak9000.spaceawaits.world.breaking;

import java.util.Random;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;

public interface IBreakable {
    
    //Does this seperation even make sense here?
    
    void collectDrops(Engine world, Random random, Entity entity, Array<ItemStack> drops);
    
    void onEntityBreak(Engine world, Entity entity, IBreaker breaker);
    
}
