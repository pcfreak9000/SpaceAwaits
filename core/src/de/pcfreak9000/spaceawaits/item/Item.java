package de.pcfreak9000.spaceawaits.item;

import java.util.Objects;

import de.pcfreak9000.spaceawaits.core.ITextureProvider;
import de.pcfreak9000.spaceawaits.core.TextureProvider;

/**
 * represents an Item
 *
 * @author pcfreak9000
 *
 */
public class Item {
    
    private ITextureProvider textureProvider;
    
    private String displayName;
    private String description;
    
    private int maxstacksize = ItemStack.MAX_STACKSIZE;
    
    public void setTexture(String name) {
        setTextureProvider(TextureProvider.get(name));
    }
    
    public void setTextureProvider(ITextureProvider prov) {
        Objects.requireNonNull(prov);
        this.textureProvider = prov;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public void setDisplayName(String name) {
        this.displayName = name;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String desc) {
        this.description = desc;
    }
    
    public int getMaxStackSize() {
        return this.maxstacksize;
    }
    
    public void setMaxStackSize(int i) {
        this.maxstacksize = i;
    }
    
    public ITextureProvider getTextureProvider() {
        return textureProvider == null ? TextureProvider.EMPTY : textureProvider;
    }
}
