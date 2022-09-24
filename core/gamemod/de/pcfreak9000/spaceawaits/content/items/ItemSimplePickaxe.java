package de.pcfreak9000.spaceawaits.content.items;

import de.pcfreak9000.spaceawaits.content.Tools;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class ItemSimplePickaxe extends Item {
    
    private static final float RANGE = 6;
    private static final int MAX_USES = 200;
    private final BreakerTools breaker = new BreakerTools(Tools.PICKAXE, 3f, 2f);
    
    public ItemSimplePickaxe() {
        this.setMaxStackSize(1);
        this.setDisplayName("Simple Pickaxe");
        this.setTexture("simplePickaxe.png");
    }
    
    @Override
    public float getMaxRangeBreakAttack(Player player, ItemStack stack) {
        return RANGE;
    }
    
    @Override
    public boolean onItemBreakTile(Player player, ItemStack stackUsed, World world, float x, float y, TileSystem tiles,
            int tx, int ty, TileLayer layer) {
        float f = tiles.breakTile(tx, ty, layer, breaker);
        return Tools.handleUsageBreaker(f, stackUsed, MAX_USES);
    }
    
}
