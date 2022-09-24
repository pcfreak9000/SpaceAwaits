package de.pcfreak9000.spaceawaits.content.items;

import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.Destructible;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.content.EntityInteractSystem;
import de.pcfreak9000.spaceawaits.world.tile.IBreaker;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class ItemCreativeBreaker extends Item {
    
    private final IBreaker breaker = new IBreaker() {
        
        @Override
        public void onBreak(World world, Destructible breakable, Array<ItemStack> drops, Random random) {
        }
        
        @Override
        public boolean canBreak(World world, Destructible breakable) {
            return true;
        }
        
        @Override
        public float breakIt(World world, Destructible breakable, float progressCurrent) {
            return Float.POSITIVE_INFINITY;
        }
    };
    
    public ItemCreativeBreaker() {
        this.setMaxStackSize(1);
        this.setDisplayName("Creative Breaker");
        this.setTexture("crebreak.png");
    }
    
    @Override
    public float getMaxRangeBreakAttack(Player player, ItemStack stack) {
        return Float.POSITIVE_INFINITY;
    }
    
    @Override
    public boolean onItemBreakAttackEntity(Player player, ItemStack stackUsed, World world, float x, float y,
            Entity entity) {
        float f = world.getSystem(EntityInteractSystem.class).breakEntity(breaker, entity);
        return f != IBreaker.ABORTED_BREAKING;
    }
    
    @Override
    public boolean onItemBreakTile(Player player, ItemStack stackUsed, World world, float x, float y, TileSystem tiles,
            int tx, int ty, TileLayer layer) {
        float f = tiles.breakTile(tx, ty, layer, breaker);
        return f != IBreaker.ABORTED_BREAKING;
    }
}
