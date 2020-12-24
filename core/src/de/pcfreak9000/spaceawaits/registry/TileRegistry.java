package de.pcfreak9000.spaceawaits.registry;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

import de.pcfreak9000.spaceawaits.tileworld.tile.Tile;

public class TileRegistry extends GameRegistry<Tile> {
    
    public void reloadResources(AssetManager assets) {
        for (Tile tile : getAll()) {
            assets.load(tile.getTextureName(), Texture.class);
        }
    }
}
