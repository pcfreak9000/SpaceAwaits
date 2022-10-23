package de.pcfreak9000.spaceawaits.content.modules;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.module.IModule;
import de.pcfreak9000.spaceawaits.module.ModuleID;

public interface IBurnModule extends IModule {
    
    public static final ModuleID ID = ModuleID.getFor(IBurnModule.class);
    
    int getBurnTime(ItemStack stack);
}
