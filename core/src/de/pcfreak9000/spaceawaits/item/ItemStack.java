package de.pcfreak9000.spaceawaits.item;

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
    
    private Item item;
    private int count;
    
    private NBTCompound nbt;
    
    public ItemStack(final Item item, final int count) {
        this.item = item;
        this.count = count;
    }
    
    public Item getItem() {
        return this.item;
    }
    
    public int getCount() {
        return this.count;
    }
    
    public boolean isEmpty() {
        return getCount() <= 0;
    }
    
    public boolean isFull() {
        return getCount() >= MAX_STACKSIZE;
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
        NBTCompound c = (NBTCompound) tag;
        String id = c.getString("id");
        this.item = GameRegistry.ITEM_REGISTRY.get(id);
        this.count = c.getByte("c");//Hopefully works with conversion...
        if (c.hasKey("nbt")) {
            this.nbt = c.getCompound("nbt");
        }
    }
    
    @Override
    public NBTTag writeNBT() {
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
