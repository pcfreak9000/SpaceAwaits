package de.pcfreak9000.spaceawaits.world;

import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;

public interface BreakableEntity {
    
    void onBreak(World world, Array<ItemStack> drops, Random random, Entity entity);
    
}
