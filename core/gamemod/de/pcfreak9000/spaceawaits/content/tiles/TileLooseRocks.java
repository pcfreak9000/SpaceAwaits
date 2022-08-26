package de.pcfreak9000.spaceawaits.content.tiles;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.content.items.Items;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemEntityFactory;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class TileLooseRocks extends Tile {
    
    public TileLooseRocks() {
        setDisplayName("Loose Rocks");
        setTexture("looseRocks.png");
    }
    
    @Override
    public void onNeighbourChange(World world, TileSystem tileSystem, int gtx, int gty, Tile newNeighbour,
            Tile oldNeighbour, int ngtx, int ngty, TileLayer layer) {
        if (ngty == gty - 1 && ngtx == gtx) {
            if (!newNeighbour.isSolid()) {
                tileSystem.removeTile(gtx, gty, layer);
                //drop item
                Entity e = ItemEntityFactory.setupItemEntity(new ItemStack(getItemTile(), 1), gtx, gty);
                world.spawnEntity(e, false);
            }
        }
    }
    
    @Override
    public Item getItemDropped() {
        return Items.LOOSEROCK;
    }
    
    @Override
    public int getDroppedQuantity() {
        return 2;
    }
    
    @Override
    public boolean canPlace(int tx, int ty, TileLayer layer, World world, TileSystem tileSystem) {
        return tileSystem.getTile(tx, ty - 1, layer).isSolid();
    }
    
    @Override
    public boolean hasCustomHitbox() {
        return true;
    }
    
    @Override
    public float[] getCustomHitbox() {
        return new float[] { 0, 0, /**/ 1, 0, /**/1, 0.3f, /**/0, 0.3f };
    }
}
