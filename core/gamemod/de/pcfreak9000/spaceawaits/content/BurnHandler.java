package de.pcfreak9000.spaceawaits.content;

import com.badlogic.gdx.utils.ObjectFloatMap;

import de.pcfreak9000.spaceawaits.content.items.Items;
import de.pcfreak9000.spaceawaits.content.tiles.Tiles;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.registry.IBurnHandler;

public class BurnHandler implements IBurnHandler {
    //TODO burnhandler sucks
    
    private ObjectFloatMap<Item> map = new ObjectFloatMap<>();
    
    public BurnHandler() {
        map.put(Items.TWIG, 1f);
        map.put(Tiles.WOOD.getItemTile(), 8f);
        map.put(Items.COAL, 16f);
    }
    
    @Override
    public float getBurnTime(Item item) {
        return map.get(item, 0);
    }
    
}
