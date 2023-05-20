package de.pcfreak9000.spaceawaits.content.tiles.primitivefurnace;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.content.modules.IBurnModule;
import de.pcfreak9000.spaceawaits.crafting.FurnaceRecipe;
import de.pcfreak9000.spaceawaits.crafting.MachineBase;
import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;
import de.pcfreak9000.spaceawaits.world.tile.ITileEntity;

public class TileEntityPrimitiveFurnace extends MachineBase implements IInventory, INBTSerializable, ITileEntity {
    
    public static final int RESULTSLOT = 2;
    public static final int INPUTSLOT = 1;
    public static final int FUELSLOT = 0;
    
    private ItemStack[] stacks = new ItemStack[3];
    
    private FurnaceRecipe currentRecipe = null;
    
    @Override
    protected int getProcessingTicksRequired() {
        return this.currentRecipe == null ? -1 : this.currentRecipe.getBurntime();
    }
    
    @Override
    protected void refuel() {
        ItemStack fuelstack = getStack(FUELSLOT);
        if (!ItemStack.isEmptyOrNull(fuelstack)) {
            IBurnModule burnm = fuelstack.getItem().getModule(IBurnModule.ID);
            int v = burnm.getBurnTime(fuelstack);
            workTicksLeft = v;
            fuelstack.changeNumber(-1);
            setSlotContent(FUELSLOT, fuelstack);
        }
    }
    
    @Override
    protected void finishProcess() {
        this.progress = 0;
        ItemStack st = getStack(RESULTSLOT);
        if (ItemStack.isEmptyOrNull(st)) {
            st = this.currentRecipe.getCraftingResult();
        } else {
            st.changeNumber(this.currentRecipe.getCraftingResult().getCount());
        }
        setSlotContent(RESULTSLOT, st);
        decrStackSize(INPUTSLOT, this.currentRecipe.getInputCount());
    }
    
    @Override
    protected boolean canProcess() {
        //TODO this kind of checking for the other craftings as well?
        ItemStack res = getStack(RESULTSLOT);
        if (currentRecipe == null) {
            return false;
        }
        if (ItemStack.isEmptyOrNull(res)) {
            return true;
        }
        return res.getCount() + this.currentRecipe.getResult().getCount() < this.currentRecipe.getResult().getMax()
                && ItemStack.isItemEqual(res, this.currentRecipe.getResult())
                && ItemStack.isStackTagEqual(res, this.currentRecipe.getResult());
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
                if (r.matches(stack)) {
                    if (this.currentRecipe != r) {
                        //currentRecipe == null -> either reading some save, or the progress is 0 anyways because the recipe was reset (see below)
                        if (this.currentRecipe != null) {
                            this.progress = 0;
                        }
                        this.currentRecipe = r;
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
        if (index == FUELSLOT) {
            return stack.getItem().hasModule(IBurnModule.ID);
        }
        return true;
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
