package de.pcfreak9000.spaceawaits.registry;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

import de.pcfreak9000.spaceawaits.item.Item;

@Deprecated
public class ItemRegistry extends GameRegistry<Item> {
    
    public void reloadResources(AssetManager assets) {
        for (Item item : getAll()) {
            assets.load(item.getTextureName(), Texture.class);
        }
    }
}
