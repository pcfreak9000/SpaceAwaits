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
    
    public static NBTTag writeNBT(ItemStack stack) {
        NBTCompound c = new NBTCompound();
        if (stack != EMPTY) {
            c.putByte("c", (byte) stack.getCount());
            String id = GameRegistry.ITEM_REGISTRY.getId(stack.getItem());
            c.putString("id", id);
            if (stack.nbt != null && !stack.nbt.entrySet().isEmpty()) {//TODO direct isEmpty in NBTCompound, also in the top of this class
                c.putCompound("nbt", stack.nbt);
            }
        }
        return c;
    }
    
    public static ItemStack readNBT(NBTTag tag) {
        if (((NBTCompound) tag).entrySet().isEmpty()) {
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
    
    //    public void changeSize(int amount) {
    //        if (this == EMPTY) {
    //            return;
    //        }
    //        int changed = this.count + amount;
    //        changed = Math.max(0, Math.min(changed, MAX_STACKSIZE));
    //        this.count = changed;
    //    }
    
    public ItemStack split(int amount) {
        if (amount <= 0 || this.isEmpty()) {
            return EMPTY;
        }
        int a = Math.min(amount, this.count);
        ItemStack s = new ItemStack(item, a);
        this.count -= a;
        if (hasNBT()) {
            s.setNBT(this.nbt.cpy());
        }
        return s;
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
