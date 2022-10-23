package de.pcfreak9000.spaceawaits.content.modules;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.module.IModule;
import de.pcfreak9000.spaceawaits.module.ModuleID;

public interface IModuleEnergy extends IModule {
    
    public static final ModuleID ID = ModuleID.getFor(IModuleEnergy.class);
    
    boolean isRechargeble(ItemStack stack);
    
    float getMaxCharge(ItemStack stack);
    
    float getCurrentCharge(ItemStack stack);
    
    void changeCharge(ItemStack stack, float change);
    
    default boolean hasCharge(ItemStack stack, float amount) {
        return getCurrentCharge(stack) - amount >= 0;
    }
    
}
