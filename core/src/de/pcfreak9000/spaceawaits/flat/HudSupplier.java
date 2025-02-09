package de.pcfreak9000.spaceawaits.flat;

import de.pcfreak9000.spaceawaits.player.InventoryPlayer;
import de.pcfreak9000.spaceawaits.world.ecs.StatsComponent;

public interface HudSupplier {
    StatsComponent getStats();

    InventoryPlayer getInventory();
}
