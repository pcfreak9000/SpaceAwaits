package de.pcfreak9000.spaceawaits.module;

import de.pcfreak9000.spaceawaits.item.ItemStack;

public interface IModuleEnergy extends IModule {
    
    public static final ModuleID ID = ModuleID.getFor(IModuleEnergy.class);
    
    boolean isRechargeble(ItemStack stack);
    
    float getMaxCharge(ItemStack stack);
    
    float getCurrentCharge(ItemStack stack);
    
    void changeCharge(ItemStack stack, float change);
    
    default boolean hasCharge(ItemStack stack, float amount) {
        return getCurrentCharge(stack) - amount >= 0;
    }
    @Deprecated
    default boolean useIfPossible(ItemStack stack, float amount) {
        if (getCurrentCharge(stack) - amount < 0) {
            return false;
        }
        changeCharge(stack, -amount);
        return true;
    }
    
}
