package de.pcfreak9000.spaceawaits.content;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.content.components.Components;
import de.pcfreak9000.spaceawaits.content.entities.Entities;
import de.pcfreak9000.spaceawaits.content.items.Items;
import de.pcfreak9000.spaceawaits.content.tiles.Tiles;
import de.pcfreak9000.spaceawaits.core.CoreEvents;
import de.pcfreak9000.spaceawaits.mod.Mod;

@Mod(id = "SpaceAwaits-Game", name = "Space Awaits Main Game", version = { 0, 0, 1 })
public class GameMod {
    @EventSubscription
    public void init(final CoreEvents.InitEvent init) {
        Components.registerComponents();
        Items.registerItems();
        Tiles.registerTiles();
        Entities.registerEntities();
    }
}