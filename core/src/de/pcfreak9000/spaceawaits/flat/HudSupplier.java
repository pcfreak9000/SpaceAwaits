package de.pcfreak9000.spaceawaits.flat;

import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.world.ecs.StatsComponent;

public interface HudSupplier {
    StatsComponent getStats();
    
    IInventory getInventory();
}
