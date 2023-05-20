package de.pcfreak9000.spaceawaits.content.modules;

import com.badlogic.gdx.math.MathUtils;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.module.ModuleBar;

public class ModuleBattery implements IModuleEnergy {
    
    private static final String KEY_CHARGE = "charge";
    
    private final float chargeMax;
    
    public ModuleBattery(float chargeMax) {
        this.chargeMax = chargeMax;
    }
    
    @Override
    public boolean isRechargeble(ItemStack stack) {
        return false;
    }
    
    @Override
    public float getMaxCharge(ItemStack stack) {
        return this.chargeMax;
    }
    
    @Override
    public float getCurrentCharge(ItemStack stack) {
        if (stack.hasNBT()) {
            return stack.getNBT().getFloat(KEY_CHARGE);
        }
        return getMaxCharge(stack);
    }
    
    @Override
    public void changeCharge(ItemStack stack, float change) {
        NBTCompound nbt = stack.getOrCreateNBT();
        float oldCharge = nbt.getFloatOrDefault(KEY_CHARGE, chargeMax);
        float newCharge = oldCharge + change;
        newCharge = MathUtils.clamp(newCharge, 0, chargeMax);
        nbt.putFloat(KEY_CHARGE, newCharge);
        if (stack.getItem().hasModule(ModuleBar.ID)) {
            ModuleBar bar = stack.getItem().getModule(ModuleBar.ID);
            bar.setValue(stack, newCharge / chargeMax);
        }
        if (newCharge <= 0f && !isRechargeble(stack)) {
            stack.changeNumber(-1);
        }
    }
    
}
