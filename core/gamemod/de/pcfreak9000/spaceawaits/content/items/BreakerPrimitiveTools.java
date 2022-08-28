package de.pcfreak9000.spaceawaits.content.items;

import java.util.Random;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.Destructible;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.IBreaker;

public class BreakerPrimitiveTools implements IBreaker {
    
    private String tool;
    
    public BreakerPrimitiveTools(String tool) {
        this.tool = tool;
    }
    
    public void setTool(String tool) {
        this.tool = tool;
    }
    
    @Override
    public float breakIt(World world, Destructible breakable, float progressCurrent) {
        return 2f / breakable.getHardness();
    }
    
    @Override
    public boolean canBreak(World world, Destructible breakable) {
        return breakable.getMaterialLevel() <= 1.0f && tool == breakable.getRequiredTool();
    }
    
    @Override
    public void onBreak(World world, Destructible breakable, Array<ItemStack> drops, Random random) {
    }
    
}
