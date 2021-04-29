package de.pcfreak9000.spaceawaits.item;

import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.spaceawaits.core.ITextureProvider;
import de.pcfreak9000.spaceawaits.core.Player;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

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
    //TODO TileLayer.Front...
    @Override
    public boolean onItemUse(Player player, ItemStack used, World world, int tilex, int tiley) {
        if (world.getTile(tilex, tiley, TileLayer.Front) == null
                || world.getTile(tilex, tiley, TileLayer.Front) == Tile.EMPTY) {
            if (!used.isEmpty()) {
                world.setTile(tilex, tiley, TileLayer.Front, this.tile);
                used.changeNumber(-1);
                return true;
            }
        }
        return false;
    }
}
