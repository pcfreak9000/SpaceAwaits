package de.pcfreak9000.spaceawaits.content;

import com.badlogic.gdx.utils.ObjectIntMap;

import de.pcfreak9000.spaceawaits.content.items.Items;
import de.pcfreak9000.spaceawaits.content.tiles.Tiles;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.registry.IBurnHandler;

public class BurnHandler implements IBurnHandler {
    //TODO burnhandler sucks
    
    private ObjectIntMap<Item> map = new ObjectIntMap<>();
    
    public BurnHandler() {
        map.put(Items.TWIG, 60);
        map.put(Tiles.WOOD.getItemTile(), 8 * 60);
        map.put(Items.COAL, 16 * 60);
    }
    
    @Override
    public int getBurnTime(Item item) {
        return map.get(item, 0);
    }
    
}
