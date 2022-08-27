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
    
    public ItemPrimitiveAxe() {
        this.setMaxStackSize(1);
        this.setDisplayName("Primitive Axe");
        this.setTexture("shitty_axe.png");
    }
    
    @Override
    public boolean onItemBreakAttackEntity(Player player, ItemStack stackUsed, World world, float x, float y,
            Entity entity) {
        BreakerPrimitiveTools.INSTANCE.setTool("axe");
        float f = world.breakEntity(BreakerPrimitiveTools.INSTANCE, entity);
        return Tools.handleUsageBreaker(f, stackUsed, 20);
    }
    
    @Override
    public boolean onItemBreakTile(Player player, ItemStack stackUsed, World world, float x, float y, TileSystem tiles,
            int tx, int ty, TileLayer layer) {
        BreakerPrimitiveTools.INSTANCE.setTool("axe");
        float f = tiles.breakTile(tx, ty, layer, BreakerPrimitiveTools.INSTANCE);
        return Tools.handleUsageBreaker(f, stackUsed, 20);
    }
    
}
