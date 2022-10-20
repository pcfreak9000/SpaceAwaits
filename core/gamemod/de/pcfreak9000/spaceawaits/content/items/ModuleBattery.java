package de.pcfreak9000.spaceawaits.content.items;

import com.badlogic.gdx.math.MathUtils;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.item.IModuleEnergy;
import de.pcfreak9000.spaceawaits.item.ItemHelper;
import de.pcfreak9000.spaceawaits.item.ItemStack;

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
        //This sucks because if charge rdrops below 0.1% (int)-cast makes it zero and then possibly removes the item
        ItemHelper.dealDamageUpdateBar(stack, (int) ((oldCharge - newCharge) / chargeMax * 10000), 10000,
                !isRechargeble(stack));
    }
    
}
