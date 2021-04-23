package de.pcfreak9000.spaceawaits.item;

public class InvUtil {
    
    public static ItemStack insert(IInventory inv, ItemStack insert) {
        ItemStack stack = insert.cpy();
        for (int i = 0; i < inv.slots(); i++) {
            if (inv.isItemValidForSlot(i, insert)) {
                ItemStack cur = inv.getStack(i);
                if (cur == null || cur.isEmpty()) {
                    inv.setSlotContent(i, stack);
                    return null;
                } else {
                    if (ItemStack.isItemEqual(cur, stack) && ItemStack.isStackTagEqual(cur, stack)) {
                        int amount = Math.min(cur.getItem().getMaxStackSize() - cur.getCount(), stack.getCount());
                        inv.setSlotContent(i, new ItemStack(stack.getItem(), amount + cur.getCount()));
                        stack.changeNumber(-amount);
                        if (stack.isEmpty()) {
                            return null;
                        }
                    }
                }
            }
        }
        return stack;
    }
    
}
