package de.pcfreak9000.spaceawaits.item;

public class InvUtil {
    
    private static ItemStack insertUnsafe(IInventory inv, int slot, ItemStack stack) {
        if (inv.isItemValidForSlot(slot, stack)) {
            ItemStack cur = inv.getStack(slot);
            if (cur == null || cur.isEmpty()) {
                inv.setSlotContent(slot, stack);
                return null;
            } else {
                if (ItemStack.isItemEqual(cur, stack) && ItemStack.isStackTagEqual(cur, stack)) {
                    int amount = Math.min(cur.getItem().getMaxStackSize() - cur.getCount(), stack.getCount());
                    inv.setSlotContent(slot, new ItemStack(stack.getItem(), amount + cur.getCount()));
                    stack.changeNumber(-amount);
                    if (stack.isEmpty()) {
                        return null;
                    }
                }
            }
        }
        return stack;
    }
    
    public static ItemStack insert(IInventory inv, int slot, ItemStack insert) {
        ItemStack stack = insert.cpy();
        return insertUnsafe(inv, slot, stack);
    }
    
    public static ItemStack insert(IInventory inv, ItemStack insert) {
        ItemStack stack = insert.cpy();
        for (int i = 0; i < inv.slots(); i++) {
            stack = insertUnsafe(inv, i, stack);
            if (stack == null) {
                return null;
            }
        }
        return stack;
    }
    
}
