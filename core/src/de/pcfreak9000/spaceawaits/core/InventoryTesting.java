package de.pcfreak9000.spaceawaits.core;

import java.util.List;

import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.registry.Registry;

public class InventoryTesting implements IInventory {
    
    private static final List<ItemStack> ALL_ITEMS = Registry.ITEM_REGISTRY.getAll().stream()
            .map((i) -> new ItemStack(i, i.getMaxStackSize())).toList();
    
    public InventoryTesting() {
    }
    
    @Override
    public int slots() {
        return ALL_ITEMS.size() + 1;
    }
    
    @Override
    public ItemStack getStack(int index) {
        return index == ALL_ITEMS.size() ? null : ALL_ITEMS.get(index);
    }
    
    @Override
    public void setSlotContent(int index, ItemStack stack) {
    }
    
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == ALL_ITEMS.size();
    }
    
}
