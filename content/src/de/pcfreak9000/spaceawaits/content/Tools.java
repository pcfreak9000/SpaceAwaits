package de.pcfreak9000.spaceawaits.content;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.module.ModuleUsage;
import de.pcfreak9000.spaceawaits.world.tile.IBreaker;

public class Tools {
    public static final String AXE = "axe";
    public static final String PICKAXE = "pickaxe";
    public static final String SHOVEL = "shovel";
    
    public static boolean handleUsageBreaker(float result, ItemStack used) {
        if (result == IBreaker.ABORTED_BREAKING) {
            return false;
        }
        if (result == IBreaker.FINISHED_BREAKING) {
            ModuleUsage us = used.getItem().getModule(ModuleUsage.ID);
            us.use(used, true);
        }
        return true;
    }
}
