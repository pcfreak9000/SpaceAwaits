package de.pcfreak9000.spaceawaits.content.tiles;

import de.pcfreak9000.spaceawaits.content.Tools;
import de.pcfreak9000.spaceawaits.content.items.Items;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class TileOreCoal extends Tile {
    public TileOreCoal() {
        this.setTexture("oreCoal.png").setDisplayName("Coal Ore").setMaterialLevel(1f).setRequiredTool(Tools.PICKAXE);
    }
    
    @Override
    public int getDroppedQuantity() {
        return 1;
    }
    
    @Override
    public Item getItemDropped() {
        return Items.COAL;
    }
}
