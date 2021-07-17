package de.pcfreak9000.spaceawaits.item;

import java.util.Objects;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;

/**
 * a Stack of {@link Item}s <br>
 * Note: this class does not implement {@link NBTSerializable} but is
 * serializable through static methods.
 * 
 * @author pcfreak9000
 *
 */
public class ItemStack {
    
    public static final int MAX_STACKSIZE = 128;
    
    public static final ItemStack EMPTY = new ItemStack();
    
    public static boolean isItemEqual(ItemStack s1, ItemStack s2) {
        return s1.getItem() == s2.getItem();
    }
    
    public static boolean isStackTagEqual(ItemStack s1, ItemStack s2) {
        return Objects.equals(s1.nbt, s2.nbt);
    }
    
    public static boolean isEmptyOrNull(ItemStack stack) {
        return stack == null || stack.isEmpty();
    }
    
    public static ItemStack join(ItemStack stack0, ItemStack stack1) {
        if (ItemStack.isEmptyOrNull(stack1)) {
            return stack0;
        }
        if (ItemStack.isEmptyOrNull(stack0)) {
            return stack1;
        }
        if (ItemStack.isItemEqual(stack0, stack1) && ItemStack.isStackTagEqual(stack0, stack1)) {
            int amount = Math.min(stack0.getItem().getMaxStackSize() - stack0.getCount(), stack1.getCount());
            stack0.changeNumber(amount);
            stack1.changeNumber(-amount);
        }
        return stack0;
    }
    
    public static NBTTag writeNBT(ItemStack stack) {
        NBTCompound c = new NBTCompound();
        if (stack != EMPTY) {
            c.putByte("c", (byte) stack.getCount());
            String id = GameRegistry.ITEM_REGISTRY.getId(stack.getItem());
            c.putString("id", id);
            if (stack.nbt != null && !stack.nbt.isEmpty()) {
                c.putCompound("nbt", stack.nbt);
            }
        }
        return c;
    }
    
    public static ItemStack readNBT(NBTTag tag) {
        if (((NBTCompound) tag).isEmpty()) {
            return EMPTY;
        }
        ItemStack stack = new ItemStack();
        NBTCompound c = (NBTCompound) tag;
        String id = c.getString("id");
        stack.count = c.getByte("c");//Hopefully works with conversion...
        stack.item = GameRegistry.ITEM_REGISTRY.get(id);
        if (c.hasKey("nbt")) {
            stack.nbt = c.getCompound("nbt");
        }
        return stack;
    }
    
    private Item item;
    private int count;
    
    private NBTCompound nbt;
    
    private ItemStack() {
        this.count = 0;
        this.item = null;
    }
    
    public ItemStack(final Item item, final int count) {
        this.item = Objects.requireNonNull(item);
        this.count = count;
    }
    
    public ItemStack cpy() {
        if (this == EMPTY) {
            return EMPTY;
        }
        ItemStack newstack = new ItemStack(item, count);
        if (hasNBT()) {
            newstack.setNBT(getNBT().cpy());
        }
        return newstack;
    }
    
    public Item getItem() {
        return this.item;
    }
    
    public int getCount() {
        return this.count;
    }
    
    public boolean isEmpty() {
        return this == EMPTY || getCount() <= 0;
    }
    
    public boolean isFull() {
        return getCount() >= MAX_STACKSIZE || getCount() >= getItem().getMaxStackSize();
    }
    
    public int changeNumber(int change) {
        int countold = this.count;
        int dest = change + countold;
        int max = this.getMax();
        int actual = Math.min(Math.max(dest, 0), max);
        this.count = actual;
        return countold + actual;
    }
    
    public int getMax() {
        if (this == EMPTY) {
            return 0;
        }
        return Math.min(getItem().getMaxStackSize(), MAX_STACKSIZE);
    }
    
    public boolean hasNBT() {
        return nbt != null;
    }
    
    public NBTCompound getOrCreateNBT() {
        if (!hasNBT()) {
            this.nbt = new NBTCompound();
        }
        return this.nbt;
    }
    
    public NBTCompound getNBT() {
        return nbt;
    }
    
    public void setNBT(NBTCompound nbt) {
        this.nbt = nbt;
    }
    
}
