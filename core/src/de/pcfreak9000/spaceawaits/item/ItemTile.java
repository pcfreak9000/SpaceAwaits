package de.pcfreak9000.spaceawaits.item;

import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.spaceawaits.core.ITextureProvider;
import de.pcfreak9000.spaceawaits.core.Player;
import de.pcfreak9000.spaceawaits.world.WorldAccessor;
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
    
    @Override
    public boolean onItemUse(Player player, ItemStack used, WorldAccessor world, int tilex, int tiley) {
        if (world.getTile(tilex, tiley) == null || world.getTile(tilex, tiley) == Tile.EMPTY) {
            if (!used.isEmpty()) {
                world.setTile(this.tile, tilex, tiley);
                used.changeNumber(-1);
                return true;
            }
        }
        return false;
    }
}
