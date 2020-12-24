package de.pcfreak9000.spaceawaits.item;

/**
 * a Stack of {@link Item}s
 *
 * @author pcfreak9000
 *
 */
public class ItemStack {
    
    public static final int MAX_STACKSIZE = 128;
    
    private final Item item;
    private final int count;
    
    public ItemStack(final Item item, final int count) {
        this.item = item;
        this.count = count;
    }
    
    public Item getItem() {
        return this.item;
    }
    
    public int getCount() {
        return this.count;
    }
    
    public boolean isEmpty() {
        return getCount() <= 0;
    }
    
    public boolean isFull() {
        return getCount() >= MAX_STACKSIZE;
    }
    
}
