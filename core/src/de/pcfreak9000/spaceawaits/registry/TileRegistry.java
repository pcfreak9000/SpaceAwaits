package de.pcfreak9000.spaceawaits.registry;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import de.pcfreak9000.spaceawaits.tileworld.tile.Tile;

public class TileRegistry extends GameRegistry<Tile> {
    
    public void reloadResources(AssetManager assets) {
        for (Tile tile : getAll()) {
            String name = tile.getTextureName();
            if (name == null || name.isEmpty()) {
                name = "missing_texture.png";
            }
            assets.load(name, Texture.class);
        }
    }
    
    public void setupTiles(AssetManager m) {
        for (Tile tile : getAll()) {
            tile.setTextureRegion(new TextureRegion(m.get(
                    tile.getTextureName() == null ? "missing_texture.png" : tile.getTextureName(), Texture.class)));
        }
    }
}
