package de.pcfreak9000.spaceawaits.content.items;

import de.pcfreak9000.spaceawaits.content.modules.IModuleEnergy;
import de.pcfreak9000.spaceawaits.content.modules.ModuleBattery;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.module.ModuleBar;

public class ItemSimpleBattery extends Item {
    public ItemSimpleBattery() {
        this.setMaxStackSize(1);
        this.setDisplayName("Simple Battery");
        this.setTexture("battery.png");
        addModule(IModuleEnergy.ID, new ModuleBattery(1000));
        addModule(ModuleBar.ID, new ModuleBar());
    }
}
