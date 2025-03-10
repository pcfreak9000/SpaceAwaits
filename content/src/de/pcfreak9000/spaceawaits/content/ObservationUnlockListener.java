package de.pcfreak9000.spaceawaits.content;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.content.items.Items;
import de.pcfreak9000.spaceawaits.world.WorldEvents.PlayerItemPickupEvent;

public class ObservationUnlockListener {

    @EventSubscription
    private void ev(PlayerItemPickupEvent ev) {
        if (ev.item == Items.TWIG) {
            ev.player.getKnowledge().unlock(GameMod.TEST_OBS);
        }
    }

}
