package de.pcfreak9000.spaceawaits.core.assets;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.CoreEvents.QueueResourcesEvent;
import de.pcfreak9000.spaceawaits.core.CoreEvents.UpdateResourcesEvent;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;

public class SkinProvider {

    // Well at some point we will see if hotswapping works

    private Skin skin;

    public SkinProvider() {
        SpaceAwaits.BUS.register(this);
    }

    public Skin getSkin() {
        return skin;
    }

    @EventSubscription
    private void resEv1(QueueResourcesEvent ev) {
        ev.assetMgr.load("ui/skin.json", Skin.class);
    }

    @EventSubscription
    private void resEv2(UpdateResourcesEvent ev) {
        this.skin = ev.assetMgr.get("ui/skin.json", Skin.class);
    }

}
