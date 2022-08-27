package de.pcfreak9000.spaceawaits.item;

import java.util.Objects;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.spaceawaits.comp.Composite;
import de.pcfreak9000.spaceawaits.core.ITextureProvider;
import de.pcfreak9000.spaceawaits.core.TextureProvider;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

/**
 * represents an Item
 *
 * @author pcfreak9000
 *
 */
public class Item {
    
    public static final float WORLD_SIZE = 0.6f;
    
    private ITextureProvider textureProvider;
    
    private int maxstacksize = ItemStack.MAX_STACKSIZE;
    
    private Color color = Color.WHITE;
    private String displayName = "";
    
    private Composite composite;
    
    public Item setTexture(String name) {
        setTextureProvider(TextureProvider.get(name));
        return this;
    }
    
    public Item setTextureProvider(ITextureProvider prov) {
        Objects.requireNonNull(prov);
        this.textureProvider = prov;
        return this;
    }
    
    public int getMaxStackSize() {
        return this.maxstacksize;
    }
    
    public void setMaxStackSize(int i) {
        this.maxstacksize = i;
    }
    
    public Item setColor(Color color) {
        this.color = color;
        return this;
    }
    
    public Color getColor() {
        return color;
    }
    
    public Item setDisplayName(String name) {
        this.displayName = name;
        return this;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public void setComposite(Composite composite) {
        this.composite = composite;
    }
    
    public Composite getComposite() {
        return composite;
    }
    
    public ITextureProvider getTextureProvider() {
        return textureProvider == null ? TextureProvider.EMPTY : textureProvider;
    }
    
    /**
     * 
     * @param player
     * @param stackUsed
     * @param world
     * @param tilex
     * @param tiley
     * @return whether this item has actually been used
     */
    public boolean onItemUse(Player player, ItemStack stackUsed, World world, float x, float y, int tilex, int tiley,
            TileLayer layer) {
        return false;
    }
    
    public boolean onItemJustUse(Player player, ItemStack stackUsed, World world, float x, float y, int tilex,
            int tiley, TileLayer layer) {
        return false;
    }
    
    public boolean onItemBreakTile(Player player, ItemStack stackUsed, World world, float x, float y, TileSystem tiles,
            int tx, int ty, TileLayer layer) {
        return false;
    }
    
    public boolean onItemBreakAttackEntity(Player player, ItemStack stackUsed, World world, float x, float y,
            Entity entity) {
        return false;
    }
    
    public boolean isSpecialBreakAttack() {
        return false;
    }
    
    public boolean onItemSpecialBreakAttack(Player player, ItemStack stackUsed, World world, float x, float y, int tx,
            int ty, TileLayer layer) {
        return false;
    }
    
    @Override
    public String toString() {
        return "Item [maxstacksize=" + maxstacksize + ", displayName=" + displayName + ", registryId="
                + GameRegistry.ITEM_REGISTRY.getId(this) + "]";
    }
    
}
