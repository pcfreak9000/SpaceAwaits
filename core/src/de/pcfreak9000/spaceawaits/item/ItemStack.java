package de.pcfreak9000.spaceawaits.item;

import java.util.Objects;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.EntityInteractSystem;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

/**
 * a Stack of {@link Item}s <br>
 * Note: this class does not implement {@link NBTSerializable} but is
 * serializable through static methods.
 * 
 * @author pcfreak9000
 *
 */
public class ItemStack {
    
    public static final int MAX_STACKSIZE = 999;
    
    public static final ItemStack EMPTY = new ItemStack() {
        @Override
        public void drop(World world, float x, float y) {
            return;
        }
    };
    
    public static void drop(Array<ItemStack> array, World world, float tx, float ty) {
        for (ItemStack s : array) {
            if (!ItemStack.isEmptyOrNull(s)) {
                s.drop(world, tx, ty);
            }
        }
    }
    
    public static void dropRandomInTile(Array<ItemStack> array, World world, float tx, float ty) {
        for (ItemStack s : array) {
            if (!ItemStack.isEmptyOrNull(s)) {
                s.dropRandomInTile(world, tx, ty);
            }
        }
    }
    
    public static boolean isItemEqual(ItemStack s1, ItemStack s2) {
        if (isEmptyOrNull(s1) || isEmptyOrNull(s2)) {
            return false;
        }
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
    
    public static NBTCompound writeNBT(ItemStack stack, NBTCompound c) {
        if (!ItemStack.isEmptyOrNull(stack)) {
            c.putShort("count", (short) stack.getCount());
            String id = Registry.ITEM_REGISTRY.getId(stack.getItem());
            c.putString("itemid", id);
            if (stack.nbt != null && !stack.nbt.isEmpty()) {
                c.putCompound("nbt", stack.nbt);
            }
        } else {
            c.putBooleanAsByte("empty", true);
        }
        return c;
    }
    
    public static ItemStack readNBT(NBTCompound tag) {
        if (tag.isEmpty() || tag.getBooleanFromByteOrDefault("empty", false)) {
            return EMPTY;
        }
        ItemStack stack = new ItemStack();
        NBTCompound c = tag;
        String id = c.getString("itemid");
        stack.count = Short.toUnsignedInt(c.getShort("count"));//Hopefully works with conversion... now it does. Dumbass forgot max stacksize is 128 and thats problematic for a signed byte
        if (Registry.ITEM_REGISTRY.isRegistered(id)) {
            stack.item = Registry.ITEM_REGISTRY.get(id);
        }
        if (stack.item == null) {
            return EMPTY;
        }
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
    
    public ItemStack(Item item) {
        this(item, 1);
    }
    
    public ItemStack(final Item item, final int count) {
        this.item = Objects.requireNonNull(item);
        this.count = count;
    }
    
    public ItemStack(Tile tile) {
        this(tile, 1);
    }
    
    public ItemStack(final Tile tile, final int count) {
        this(tile.getItemTile(), count);
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
    
    public ItemStack sub(int count) {
        ItemStack stack = this.cpy();
        int old = this.getCount();
        this.changeNumber(-count);
        int actDif = old - this.getCount();
        stack.setCount(actDif);
        if (stack.isEmpty() || actDif <= 0) {
            return EMPTY;
        }
        return stack;
    }
    
    public int changeNumber(int change) {
        int countold = this.count;
        int dest = change + countold;
        int max = this.getMax();
        int actual = Math.min(Math.max(dest, 0), max);
        this.count = actual;
        return countold + actual;//WTF is happening?
    }
    
    public void setCount(int count) {
        this.count = Math.min(Math.max(count, 0), getMax());
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
    
    public void dropRandomInTile(World world, float x, float y) {
        x = x + world.getWorldRandom().nextFloat() * 0.9f;
        y = y + world.getWorldRandom().nextFloat() * 0.9f;
        drop(world, x, y);
    }
    
    public void drop(World world, float x, float y) {
        Entity e = ItemEntityFactory.setupItemEntity(this, x - Item.WORLD_SIZE / 2.1f, y - Item.WORLD_SIZE / 2.1f);
        world.getSystem(EntityInteractSystem.class).spawnEntity(e, false);
    }
    
    @Override
    public String toString() {
        return "ItemStack [item=" + Objects.toString(item) + ", count=" + count + ", hasNBT=" + hasNBT() + "]";
    }
    
}
