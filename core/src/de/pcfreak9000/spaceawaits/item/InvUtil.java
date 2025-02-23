package de.pcfreak9000.spaceawaits.item;

import com.badlogic.gdx.utils.IntArray;

import de.pcfreak9000.spaceawaits.crafting.OreDictStack;
import de.pcfreak9000.spaceawaits.registry.OreDict;

public class InvUtil {
    
    //Illegal stack sizes are pruned, so items can get lost
    private static ItemStack insertUnsafe(IInventory inv, int slot, ItemStack stack) {
        if (inv.isItemValidForSlot(slot, stack)) {
            ItemStack cur = inv.getStack(slot);
            if (ItemStack.isEmptyOrNull(cur)) {
                inv.setSlotContent(slot, stack);
                return null;
            } else {
                if (ItemStack.isItemEqual(cur, stack) && ItemStack.isStackTagEqual(cur, stack)) {
                    int amount = Math.min(cur.getItem().getMaxStackSize() - cur.getCount(), stack.getCount());
                    //FIXME stacktag is not transferred to new stack
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
    
    public static boolean canInsert(IInventory inv, int slot, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return true;
        }
        if (inv.isItemValidForSlot(slot, stack)) {
            ItemStack cur = inv.getStack(slot);
            if (cur == null || cur.isEmpty()) {
                return true;
            }
            return ItemStack.isItemEqual(cur, stack) && ItemStack.isStackTagEqual(cur, stack);
        }
        return false;
    }
    
    public static ItemStack insert(IInventory inv, int slot, ItemStack insert) {
        if (insert == null || insert.isEmpty()) {
            return null;
        }
        ItemStack stack = insert.cpy();
        return insertUnsafe(inv, slot, stack);
    }
    
    public static ItemStack insert(IInventory inv, ItemStack insert) {
        if (insert == null || insert.isEmpty()) {
            return null;
        }
        ItemStack stack = insert.cpy();
        for (int i = 0; i < inv.slots(); i++) {
            stack = insertUnsafe(inv, i, stack);
            if (stack == null) {
                return null;
            }
        }
        return stack;
    }
    
    public static ItemStack extract(IInventory inv, int slot) {
        return inv.removeStack(slot);
    }
    
    public static IntArray findEmptySlots(IInventory inv) {
        IntArray ar = new IntArray(false, 1);
        for (int i = 0; i < inv.slots(); i++) {
            if (ItemStack.isEmptyOrNull(inv.getStack(i))) {
                ar.add(i);
            }
        }
        ar.shrink();
        return ar;
    }
    
    public static boolean removeItemCount(IInventory inv, ItemStack rem) {
        for (int i = 0; i < inv.slots(); i++) {
            ItemStack content = inv.getStack(i);
            if (ItemStack.isItemEqual(rem, content) && content.getCount() >= rem.getCount()) {
                content.changeNumber(-rem.getCount());
                inv.setSlotContent(i, content);
                return true;
            }
        }
        return false;
    }
    
    public static boolean removeItemCount(IInventory inv, OreDictStack stack) {
        IntArray array = new IntArray();
        int counttmp = stack.getCount();
        int count = stack.getCount();
        for (int i = 0; i < inv.slots(); i++) {
            ItemStack content = inv.getStack(i);
            if (OreDict.isItemEqual(stack.getName(), content)) {
                array.add(i);
                count -= content.getCount();
                //content.changeNumber(-rem.getCount());
                //inv.setSlotContent(i, content);
                //return true;
            }
            if (count <= 0) {
                break;
            }
        }
        if (count > 0) {
            return false;
        }
        for (int i = 0; i < array.size; i++) {
            int stackindex = array.get(i);
            ItemStack content = inv.getStack(stackindex);
            int x = content.getCount();
            content.changeNumber(-counttmp);
            counttmp -= (x - content.getCount());
            if (counttmp <= 0) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean containsItemCount(IInventory inv, ItemStack item) {
        for (int i = 0; i < inv.slots(); i++) {
            if (ItemStack.isItemEqual(item, inv.getStack(i)) && inv.getStack(i).getCount() >= item.getCount()) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean containsItemCount(IInventory inv, OreDictStack stack) {
        int count = stack.getCount();
        for (int i = 0; i < inv.slots(); i++) {
            if (OreDict.isItemEqual(stack.getName(), inv.getStack(i))) {
                count -= inv.getStack(i).getCount();
            }
            if (count <= 0) {
                return true;
            }
        }
        return false;
    }
    
    //    public static void sort(IInventory inv, int begIncl, int endExcl) {
    //        //does nothing right now
    //    }
    
}
