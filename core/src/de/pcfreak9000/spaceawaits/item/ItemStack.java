package de.pcfreak9000.spaceawaits.item;

import java.util.Objects;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;

/**
 * a Stack of {@link Item}s
 *
 * @author pcfreak9000
 *
 */
public class ItemStack implements NBTSerializable {
    
    public static final int MAX_STACKSIZE = 128;
    
    public static final ItemStack EMPTY = new ItemStack();
    
    private Item item;
    private int count;
    
    private NBTCompound nbt;
    
    //    public static NBTTag writeNBT(ItemStack stack) {
    //        return stack.writeNBT();
    //    }
    //    
    //    public static ItemStack readNBT_s(NBTTag tag) {
    //        ItemStack stack = new ItemStack(null, 0);
    //        stack.readNBT(tag);
    //        return stack;
    //    }
    
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
    
    public boolean isFull() {//TODO isFull
        return getCount() >= MAX_STACKSIZE;
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
    
    @Override
    public void readNBT(NBTTag tag) {
        //Empty ItemStack, also what if this == EMPTY?
        NBTCompound c = (NBTCompound) tag;
        String id = c.getString("id");
        this.count = c.getByte("c");//Hopefully works with conversion...
        this.item = GameRegistry.ITEM_REGISTRY.get(id);
        if (c.hasKey("nbt")) {
            this.nbt = c.getCompound("nbt");
        }
    }
    
    @Override
    public NBTTag writeNBT() {
        //TODO what if this itemstack is just empty?
        NBTCompound c = new NBTCompound();
        String id = GameRegistry.ITEM_REGISTRY.getId(getItem());
        c.putString("id", id);
        c.putByte("c", (byte) getCount());
        if (this.nbt != null && !this.nbt.entrySet().isEmpty()) {//TODO direct isEmpty in NBTCompound
            c.putCompound("nbt", this.nbt);
        }
        return c;
    }
    
}
