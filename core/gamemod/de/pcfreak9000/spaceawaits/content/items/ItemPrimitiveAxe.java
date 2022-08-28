package de.pcfreak9000.spaceawaits.content.items;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.content.Tools;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class ItemPrimitiveAxe extends Item {
    
    private static final float RANGE = 5;
    private static final int MAX_USES = 20;
    
    private final BreakerPrimitiveTools breaker = new BreakerPrimitiveTools(Tools.AXE);
    
    public ItemPrimitiveAxe() {
        this.setMaxStackSize(1);
        this.setDisplayName("Primitive Axe");
        this.setTexture("shitty_axe.png");
    }
    
    @Override
    public float getMaxRangeBreakAttack(Player player, ItemStack stack) {
        return RANGE;
    }
    
    @Override
    public boolean onItemBreakAttackEntity(Player player, ItemStack stackUsed, World world, float x, float y,
            Entity entity) {
        float f = world.breakEntity(breaker, entity);
        return Tools.handleUsageBreaker(f, stackUsed, MAX_USES);
    }
    
    @Override
    public boolean onItemBreakTile(Player player, ItemStack stackUsed, World world, float x, float y, TileSystem tiles,
            int tx, int ty, TileLayer layer) {
        float f = tiles.breakTile(tx, ty, layer, breaker);
        return Tools.handleUsageBreaker(f, stackUsed, MAX_USES);
    }
    
}
