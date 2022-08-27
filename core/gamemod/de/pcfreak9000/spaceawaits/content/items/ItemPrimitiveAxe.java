package de.pcfreak9000.spaceawaits.content.items;

import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.Breakable;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.IBreaker;

public class ItemPrimitiveAxe extends Item {
    
    public ItemPrimitiveAxe() {
        this.setMaxStackSize(1);
        this.setDisplayName("Primitive Axe");
        this.setTexture("shitty_axe.png");
    }
    
    @Override
    public boolean onItemBreakAttackEntity(Player player, ItemStack stackUsed, World world, float x, float y,
            Entity entity) {
        return world.breakEntity(tilebreaker, entity);
    }
    
    private final IBreaker tilebreaker = new IBreaker() {
        
        @Override
        public float breakIt(World world, Breakable breakable, float f) {
            return 15f / breakable.getHardness();
        }
        
        @Override
        public boolean canBreak(World world, Breakable breakable) {
            return true;
        }
        
        @Override
        public void onBreak(World world, Breakable breakable, Array<ItemStack> drops, Random random) {
        }
        
    };
}
