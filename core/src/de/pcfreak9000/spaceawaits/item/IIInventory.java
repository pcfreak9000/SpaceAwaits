package de.pcfreak9000.spaceawaits.item;
/**
 * Stores items in slots.
 * 
 * @author pcfreak9000
 *
 */
@Deprecated
public interface IIInventory {
    
    public static enum Acceptance {
        Full, None, Partial;
    }
    
    /**
     * Returns the ItemStack in the specified slot or null if there is none.
     * 
     * @param index index of the slot
     * @return the itemstack in slot index or null if there is none
     */
    ItemStack getStackInSlot(int index);
    
    /**
     * The number of slots in this Inventory.
     * 
     * @return number of slots in this Inventory
     */
    int max();
    
    /**
     * The number of occupied slots in this Inventory.
     * 
     * @return number of occupied slots in this Inventory
     */
    int occupied();
    
    ItemStack decrStack(int index, int count);
    
    /**
     * Tries to add the given stack to this Inventory.
     * 
     * @param stack
     * @return leftovers or null if none
     */
    ItemStack add(ItemStack stack);
    
    /**
     * Checks if there is place in this Inventory for the given ItemStack.
     * 
     * @param stack the ItemStack
     * @return Acceptance
     */
    Acceptance accepts(ItemStack stack);
    
    /**
     * Tries to add the given stack to this Inventory into and only into the
     * specified slot.
     * 
     * @param index index of the slot
     * @param stack the ItemStack
     * @return leftovers or null if none
     */
    ItemStack add(int index, ItemStack stack);
    
    /**
     * Checks if there is place in this Inventory at and only at the specified slot
     * for the given ItemStack.
     * 
     * @param index the index of the slot
     * @param stack the ItemStack
     * @return Acceptance
     */
    Acceptance accepts(int index, ItemStack stack);
}
