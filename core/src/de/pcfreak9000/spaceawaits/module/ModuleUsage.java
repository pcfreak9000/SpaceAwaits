package de.pcfreak9000.spaceawaits.module;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public class ModuleUsage implements IModule {
    
    public static final ModuleID ID = ModuleID.getFor(ModuleUsage.class);
    
    private final int max;
    
    public ModuleUsage(int max) {
        this.max = max;
    }
    
    public int getMax(ItemStack stack) {
        return max;
    }
    
    public boolean hasUsesLeft(ItemStack stack) {
        if (!stack.hasNBT() || !stack.getNBT().hasKey("uses")) {
            return true;
        }
        return stack.getNBT().getInt("uses") > 0;
    }
    
    public void use(ItemStack stack, boolean removeIfDead) {
        int max = getMax(stack);
        NBTCompound nbt = stack.getOrCreateNBT();
        nbt.putInt("usesMax", max);
        int usesNew = nbt.getIntOrDefault("uses", max) - 1;
        nbt.putInt("uses", usesNew);
        if (stack.getItem().hasModule(ModuleBar.ID)) {
            ModuleBar bar = stack.getItem().getModule(ModuleBar.ID);
            bar.setValue(stack, usesNew / (float) max);
        }
        if (removeIfDead && usesNew <= 0) {
            stack.changeNumber(-1);
        }
    }
    
}
