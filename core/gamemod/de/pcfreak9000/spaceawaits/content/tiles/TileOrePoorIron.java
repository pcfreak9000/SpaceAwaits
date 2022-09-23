package de.pcfreak9000.spaceawaits.content.tiles;

import de.pcfreak9000.spaceawaits.content.Tools;
import de.pcfreak9000.spaceawaits.content.items.Items;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class TileOrePoorIron extends Tile {
    public TileOrePoorIron() {
        this.setTexture("ore_iron.png").setDisplayName("Poor Iron Ore").setMaterialLevel(1f).setRequiredTool(Tools.PICKAXE);
    }
    
    @Override
    public Item getItemDropped() {
        return Items.CLUMP_ORE_IRON;
    }
}
