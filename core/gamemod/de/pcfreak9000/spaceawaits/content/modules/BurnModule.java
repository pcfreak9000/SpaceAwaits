package de.pcfreak9000.spaceawaits.content.modules;

import de.pcfreak9000.spaceawaits.item.ItemStack;

public class BurnModule implements IBurnModule {
    
    private final int burntime;
    
    public BurnModule(int bt) {
        this.burntime = bt;
    }
    
    @Override
    public int getBurnTime(ItemStack stack) {
        return this.burntime;
    }
    
}
