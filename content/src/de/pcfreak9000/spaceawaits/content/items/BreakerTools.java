package de.pcfreak9000.spaceawaits.content.items;

import java.util.Random;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.breaking.BreakableInfo;
import de.pcfreak9000.spaceawaits.world.breaking.IBreaker;

public class BreakerTools implements IBreaker {
    
    private String tool;
    private float level;
    private float basespeed;
    
    public BreakerTools(String tool, float basespeed, float level) {
        this.tool = tool;
        this.basespeed = basespeed;
        this.level = level;
    }
    
    public void setTool(String tool) {
        this.tool = tool;
    }
    
    @Override
    public float breakIt(Engine world, BreakableInfo breakable, float progressCurrent) {
        return basespeed / breakable.getHardness();
    }
    
    @Override
    public boolean canBreak(Engine world, BreakableInfo breakable) {
        return breakable.getMaterialLevel() <= level && tool == breakable.getRequiredTool();
    }
    
    @Override
    public void onBreak(Engine world, BreakableInfo breakable, Array<ItemStack> drops, Random random) {
    }
    
}
