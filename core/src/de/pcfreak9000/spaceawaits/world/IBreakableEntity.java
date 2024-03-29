package de.pcfreak9000.spaceawaits.world;

import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.tile.IBreaker;

public interface IBreakableEntity {
    
    //Does this seperation even make sense here?
    
    void collectDrops(World world, Random random, Entity entity, Array<ItemStack> drops);
    
    void onEntityBreak(World world, Entity entity, IBreaker breaker);
    
}
