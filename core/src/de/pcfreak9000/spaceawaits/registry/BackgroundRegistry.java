package de.pcfreak9000.spaceawaits.registry;

import com.badlogic.gdx.assets.AssetManager;

import de.pcfreak9000.spaceawaits.tileworld.Background;

public class BackgroundRegistry extends GameRegistry<Background> {
    
    public void reloadResources(AssetManager assets) {
        for (Background background : getAll()) {
            //assets.load(tile.getTextureName(), Texture.class);
        }
    }
    
}
