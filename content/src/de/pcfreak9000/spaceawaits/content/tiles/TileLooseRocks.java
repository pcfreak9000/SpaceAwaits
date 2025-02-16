package de.pcfreak9000.spaceawaits.content.tiles;

import java.util.Random;

import com.badlogic.ashley.core.Engine;

import de.pcfreak9000.spaceawaits.content.items.Items;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class TileLooseRocks extends Tile {
    
    public TileLooseRocks() {
        setDisplayName("Loose Rocks");
        setTexture("looseRocks.png");
        setHardness(0.1f);
        setLightTransmission(0.95f);
        setFullTile(false);
    }
    
    @Override
    public void onNeighbourChange(Engine world, TileSystem tileSystem, int gtx, int gty, Tile newNeighbour,
            Tile oldNeighbour, int ngtx, int ngty, TileLayer layer, Random random) {
        if (!canPlace(gtx, gty, layer, world, tileSystem)) {
            tileSystem.removeTile(gtx, gty, layer);
            dropAsItemsInWorld(world, random, gtx, gty, layer);
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
    public boolean canPlace(int tx, int ty, TileLayer layer, Engine world, TileSystem tileSystem) {
        Tile below = tileSystem.getTile(tx, ty - 1, layer);
        return below != null && below.isSolid() && below.isFullTile();
    }
    
    @Override
    public boolean hasCustomHitbox() {
        return true;
    }
    
    @Override
    public float[] getCustomHitbox() {
        return new float[] { 0, 0, /**/ 1, 0, /**/1, 0.2f, /**/0, 0.2f };
    }
}
