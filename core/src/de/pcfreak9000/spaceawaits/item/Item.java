package de.pcfreak9000.spaceawaits.item;

import java.util.Objects;

import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.spaceawaits.core.ITextureProvider;
import de.pcfreak9000.spaceawaits.core.Player;
import de.pcfreak9000.spaceawaits.core.TextureProvider;
import de.pcfreak9000.spaceawaits.world.WorldAccessor;

/**
 * represents an Item
 *
 * @author pcfreak9000
 *
 */
public class Item {
    
    private ITextureProvider textureProvider;
    
    private int maxstacksize = ItemStack.MAX_STACKSIZE;
    
    private Color color = new Color(1, 1, 1, 1);
    
    public void setTexture(String name) {
        setTextureProvider(TextureProvider.get(name));
    }
    
    public void setTextureProvider(ITextureProvider prov) {
        Objects.requireNonNull(prov);
        this.textureProvider = prov;
    }
    
    public int getMaxStackSize() {
        return this.maxstacksize;
    }
    
    public void setMaxStackSize(int i) {
        this.maxstacksize = i;
    }
    
    public Color color() {
        return this.color;
    }
    
    public ITextureProvider getTextureProvider() {
        return textureProvider == null ? TextureProvider.EMPTY : textureProvider;
    }
    
    public boolean onItemUse(Player player, ItemStack stackUsed, WorldAccessor world, int tilex, int tiley) {
        return false;
    }
}
