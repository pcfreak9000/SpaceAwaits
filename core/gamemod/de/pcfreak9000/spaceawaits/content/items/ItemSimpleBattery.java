package de.pcfreak9000.spaceawaits.content.items;

import de.pcfreak9000.spaceawaits.item.IModuleEnergy;
import de.pcfreak9000.spaceawaits.item.Item;

public class ItemSimpleBattery extends Item {
    public ItemSimpleBattery() {
        addModule(IModuleEnergy.ID, new ModuleBattery(1000));
        this.setMaxStackSize(1);
        this.setDisplayName("Simple Battery");
        this.setTexture("battery.png");
    }
}
