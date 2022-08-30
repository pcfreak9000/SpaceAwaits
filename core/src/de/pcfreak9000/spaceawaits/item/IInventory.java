package de.pcfreak9000.spaceawaits.item;

/**
 * Represents an abstract bundle of items.
 * 
 * @author pcfreak9000
 *
 */
public interface IInventory {
    
    /**
     * The amount of slots in this Inventory.
     * 
     * @return slots
     */
    int slots();
    
    /**
     * Returns the contents of the specified slot or null if it's empty. <br>
     * Don't modify the returned ItemStack.
     * 
     * @param index slot
     * @return content in the given slot or null if none
     */
    ItemStack getStack(int index);
    
    /**
     * Removes the contents of the specified slot and returns them.
     * 
     * @param index slot
     * @return previous content in the given slot or null if none
     */
    default ItemStack removeStack(int index) {
        ItemStack stack = getStack(index);
        setSlotContent(index, null);
        return stack;
    }
    
    /**
     * Sets the content of the specified slot. Discards any previous content.
     * 
     * @param index slots
     * @param stack new content
     */
    void setSlotContent(int index, ItemStack stack);
    
    /**
     * Checks if the specified slot accepts the Items in the given stack. Ignores
     * the current contents of that slot.
     * 
     * @param index slots
     * @param stack items
     * @return true if the item is valid for the given slot
     */
    boolean isItemValidForSlot(int index, ItemStack stack);
}
