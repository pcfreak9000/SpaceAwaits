package de.pcfreak9000.spaceawaits.item;

import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.spaceawaits.core.ITextureProvider;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class ItemTile extends Item {
    private final Tile tile;
    
    public ItemTile(Tile tile) {
        this.tile = tile;
    }
    
    @Override
    public Color color() {
        return this.tile.color();
    }
    
    @Override
    public ITextureProvider getTextureProvider() {
        return this.tile.getTextureProvider();
    }
}
