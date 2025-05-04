package de.pcfreak9000.spaceawaits.content;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.content.items.Items;
import de.pcfreak9000.spaceawaits.knowledge.Knowledge;
import de.pcfreak9000.spaceawaits.knowledge.Knowledgebase;
import de.pcfreak9000.spaceawaits.knowledge.UnlockProgress;
import de.pcfreak9000.spaceawaits.world.WorldEvents.PlayerItemPickupEvent;

public class TestObservation extends Knowledge {
    public static class TestProgress extends UnlockProgress {
        
        public TestProgress(Knowledge knowledge, Knowledgebase knowledgebase) {
            super(knowledge, knowledgebase);
        }
        
        @EventSubscription
        private void ev(PlayerItemPickupEvent ev) {
            if (ev.item == Items.TWIG) {
                this.unlockKnowledge();
            }
        }
    }
    
    public TestObservation() {
        setDisplayName("Twig pickup observation");
    }
    
    @Override
    public boolean hasData() {
        return true;
    }
    
    @Override
    public UnlockProgress createDataHolder(Knowledgebase kb) {
        return new TestProgress(this, kb);
    }
    
}
