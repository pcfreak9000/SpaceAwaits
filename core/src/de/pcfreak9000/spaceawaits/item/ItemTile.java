package de.pcfreak9000.spaceawaits.item;

import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.spaceawaits.comp.Composite;
import de.pcfreak9000.spaceawaits.core.ITextureProvider;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class ItemTile extends Item {
    private final Tile tile;
    
    public ItemTile(Tile tile) {
        this.tile = tile;
    }
    
    @Override
    public Color getColor() {
        return this.tile.getColor();
    }
    
    @Override
    public ITextureProvider getIcon(ItemStack stack) {
        return this.tile.getIcon(stack);
    }
    
    @Override
    public Composite getComposite() {
        return this.tile.getComposite();
    }
    
    @Override
    public String getDisplayName() {
        return this.tile.getDisplayName();
    }
    
    @Override
    public float getMaxRangeUse(Player player, ItemStack stackUsed) {
        return player.getReach();
    }
    
    @Override
    public boolean onItemUse(Player player, ItemStack used, World world, float x, float y, int tilex, int tiley,
            TileLayer layer) {
        if (!used.isEmpty()) {
            if (world.getSystem(TileSystem.class).placeTile(tilex, tiley, layer, this.tile) != null) {
                used.changeNumber(-1);
                return true;
            }
        }
        return false;
    }
}
