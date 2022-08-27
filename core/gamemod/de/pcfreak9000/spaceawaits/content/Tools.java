package de.pcfreak9000.spaceawaits.content;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.tile.IBreaker;

public class Tools {
    public static final String AXE = "axe";
    public static final String PICKAXE = "pickaxe";
    
    public static boolean handleUsageBreaker(float result, ItemStack used, int maxuses) {
        if (result == IBreaker.ABORTED_BREAKING) {
            return false;
        }
        if (result == IBreaker.FINISHED_BREAKING) {
            used.dealDamageUpdateBar(1, maxuses, true);
        }
        return true;
    }
}
