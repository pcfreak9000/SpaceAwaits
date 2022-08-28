package de.pcfreak9000.spaceawaits.content.items;

import de.pcfreak9000.spaceawaits.content.Tools;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class ItemPrimitivePickaxe extends Item {
    
    private final BreakerPrimitiveTools breaker = new BreakerPrimitiveTools(Tools.PICKAXE);
    
    public ItemPrimitivePickaxe() {
        this.setMaxStackSize(1);
        this.setDisplayName("Primitive Pickaxe");
        this.setTexture("shitty_pickaxe.png");
    }
    
    @Override
    public boolean onItemBreakTile(Player player, ItemStack stackUsed, World world, float x, float y, TileSystem tiles,
            int tx, int ty, TileLayer layer) {
        float f = tiles.breakTile(tx, ty, layer, breaker);
        return Tools.handleUsageBreaker(f, stackUsed, 20);
    }
    
}
