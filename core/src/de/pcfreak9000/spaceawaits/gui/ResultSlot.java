package de.pcfreak9000.spaceawaits.gui;

import de.pcfreak9000.spaceawaits.item.IInventory;

public class ResultSlot extends Slot {
    
    public ResultSlot(IInventory invBacking, int index) {
        super(invBacking, index);
    }
    
    @Override
    public boolean canPut() {
        return false;
    }
}
