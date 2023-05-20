package de.pcfreak9000.spaceawaits.content.tiles;

import de.pcfreak9000.spaceawaits.content.Tools;
import de.pcfreak9000.spaceawaits.content.items.Items;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class TileOreCopper extends Tile {
    public TileOreCopper() {
        this.setTexture("oreCopper.png").setDisplayName("Copper Ore").setMaterialLevel(2f)
                .setRequiredTool(Tools.PICKAXE);
    }
    
    @Override
    public Item getItemDropped() {
        return Items.CLUMP_ORE_COPPER;
    }
}
