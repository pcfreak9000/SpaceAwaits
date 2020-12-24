package de.pcfreak9000.spaceawaits.item;

/**
 * represents an Item
 *
 * @author pcfreak9000
 *
 */
public class Item {
    
    private String textureName;
    
    private String displayName;
    private String description;
    private int maxstacksize = ItemStack.MAX_STACKSIZE;
    
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
    
    public String getTextureName() {
        return textureName;
    }
}
