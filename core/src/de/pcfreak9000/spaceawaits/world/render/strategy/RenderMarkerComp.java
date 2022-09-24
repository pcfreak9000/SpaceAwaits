package de.pcfreak9000.spaceawaits.world.render.strategy;

import com.badlogic.ashley.core.Component;

public abstract class RenderMarkerComp implements Component {
    
    private final int hash;
    public final float layeroffset;
    
    public RenderMarkerComp() {
        this(0);
    }
    
    public RenderMarkerComp(float layeroffset) {
        this.hash = System.identityHashCode(this);
        this.layeroffset = layeroffset;
    }
    
    @Override
    public int hashCode() {
        return hash;
    }
}
