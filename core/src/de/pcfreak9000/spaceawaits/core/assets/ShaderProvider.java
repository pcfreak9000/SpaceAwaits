package de.pcfreak9000.spaceawaits.core.assets;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.CoreEvents;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.core.CoreEvents.QueueResourcesEvent;
import de.pcfreak9000.spaceawaits.core.CoreEvents.UpdateResourcesEvent;

public class ShaderProvider {
    
    private String name;
    private ShaderProgram shader;
    
    public ShaderProvider(String name) {
        this.name = name;
        SpaceAwaits.BUS.register(this);
    }
    
    public ShaderProgram getShader() {
        return shader;
    }
    
    @EventSubscription
    private void resEv1(QueueResourcesEvent ev) {
        ev.assetMgr.load(name, ShaderProgram.class);
    }
    
    @EventSubscription
    private void resEv2(UpdateResourcesEvent ev) {
        this.shader = ev.assetMgr.get(name, ShaderProgram.class);
    }
}
