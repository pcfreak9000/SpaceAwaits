package de.pcfreak9000.spaceawaits.item;
@Deprecated
public class ItemStackReadable {
    private ItemStack stack;
    private IInventory origin;
    private int slot;
    
    public ItemStackReadable(ItemStack stack, IInventory inv, int slot) {
        this.stack = stack;
        this.origin = inv;
        this.slot = slot;
    }
    
    public ItemStack cpy() {
        return stack.cpy();
    }
    
    public Item getItem() {
        return stack.getItem();
    }
    
    public int getCount() {
        return stack.getCount();
    }
    
    public int changeNumber(int change) {
        int res = stack.changeNumber(change);
        origin.setSlotContent(slot, stack);
        return res;
    }
}
