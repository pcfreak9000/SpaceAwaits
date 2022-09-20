package de.pcfreak9000.spaceawaits.content.tiles;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.crafting.FurnaceRecipe;
import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;
import de.pcfreak9000.spaceawaits.serialize.NBTSerialize;
import de.pcfreak9000.spaceawaits.world.tile.ITileEntity;
import de.pcfreak9000.spaceawaits.world.tile.Tickable;

public class TileEntityPrimitiveFurnace implements IInventory, INBTSerializable, ITileEntity, Tickable {
    
    public static final int RESULTSLOT = 2;
    public static final int INPUTSLOT = 1;
    public static final int FUELSLOT = 0;
    
    private ItemStack[] stacks = new ItemStack[3];
    
    private FurnaceRecipe currentRecipe = null;
    
    @NBTSerialize(key = "burntimeLeft")
    private float partialBurnTimeLeft;
    
    @NBTSerialize(key = "prog")
    private float progress;
    
    public float getRelativeProgress() {
        if (this.currentRecipe == null) {
            return 0;
        }
        return progress / this.currentRecipe.getBurntime();
    }
    
    @Override
    public void tick(float dtime, long tickIndex) {
        ItemStack res = this.stacks[RESULTSLOT];
        //TODO this kind of checking for the other craftings as well?
        boolean freeresult = ItemStack.isEmptyOrNull(res) || (this.currentRecipe != null
                && res.getCount() + this.currentRecipe.getResult().getCount() < this.currentRecipe.getResult().getMax()
                && ItemStack.isItemEqual(res, this.currentRecipe.getResult())
                && ItemStack.isStackTagEqual(res, this.currentRecipe.getResult()));
        if (!freeresult) {
            this.progress = 0;
        }
        if (currentRecipe != null && freeresult) {
            if (partialBurnTimeLeft <= 0) {
                //refuel but only if there is an active recipe
                partialBurnTimeLeft = 1;//ah yes, magic auto refuel
            }
            //increase progress
            this.progress += dtime;
            //check for interuptions or put the result
            if (this.progress >= this.currentRecipe.getBurntime()) {
                decrStackSize(INPUTSLOT, 1);
                this.progress = 0;
                ItemStack st = getStack(RESULTSLOT);
                if (ItemStack.isEmptyOrNull(st)) {
                    st = this.currentRecipe.getCraftingResult();
                } else {
                    st.changeNumber(this.currentRecipe.getCraftingResult().getCount());
                }
                setSlotContent(RESULTSLOT, st);
            }
        }
        //remove burntime if there is any left
        if (partialBurnTimeLeft > 0) {
            partialBurnTimeLeft = Math.max(0, partialBurnTimeLeft - dtime);
        }
    }
    
    @Override
    public int slots() {
        return 3;
    }
    
    @Override
    public ItemStack getStack(int index) {
        return stacks[index];
    }
    
    @Override
    public void setSlotContent(int index, ItemStack stack) {
        stacks[index] = stack;
        if (index == INPUTSLOT) {
            checkCurrentRecipe();
        }
    }
    
    private void checkCurrentRecipe() {
        ItemStack stack = getStack(INPUTSLOT);
        if (!ItemStack.isEmptyOrNull(stack)) {
            boolean found = false;
            for (FurnaceRecipe r : FurnaceRecipe.getRecipes()) {
                System.out.println(r.getInput());
                if (ItemStack.isItemEqual(stack, r.getInput())) {
                    if (this.currentRecipe != r) {
                        this.currentRecipe = r;
                        this.progress = 0;
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                this.currentRecipe = null;
                this.progress = 0;
            }
        } else {
            this.currentRecipe = null;
            this.progress = 0;
        }
    }
    
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;//Check for fuel?
    }
    
    @Override
    public void readNBT(NBTCompound c) {
        for (int i = 0; i < stacks.length; i++) {
            if (c.hasKey("h" + i)) {
                stacks[i] = ItemStack.readNBT(c.getCompound("h" + i));
            }
        }
        checkCurrentRecipe();
    }
    
    @Override
    public void writeNBT(NBTCompound c) {
        for (int i = 0; i < stacks.length; i++) {
            ItemStack st = stacks[i];
            if (st != null && !st.isEmpty()) {
                c.put("h" + i, ItemStack.writeNBT(st, new NBTCompound()));
            }
        }
    }
    
}
