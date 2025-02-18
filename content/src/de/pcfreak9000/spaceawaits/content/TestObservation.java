package de.pcfreak9000.spaceawaits.content;

import com.badlogic.ashley.core.Engine;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.content.items.Items;
import de.pcfreak9000.spaceawaits.science.Observation;
import de.pcfreak9000.spaceawaits.science.ObservationHook;
import de.pcfreak9000.spaceawaits.science.Science;
import de.pcfreak9000.spaceawaits.world.WorldEvents.PlayerItemPickupEvent;

public class TestObservation extends Observation {
    public TestObservation() {
        setDisplayName("Twig pickup observation");
    }
    
    @Override
    public ObservationHook createHook(int envid, Engine engine, Science science) {
        return new ObservationHook(this, science) {
            @EventSubscription
            private void ev(PlayerItemPickupEvent ev) {
                if (ev.item == Items.TWIG) {
                    science.unlock(observation);
                }
            }
            
        };
    }
}
