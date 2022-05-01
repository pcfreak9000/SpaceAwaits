package de.pcfreak9000.spaceawaits.world.render.strategy;

import com.badlogic.ashley.core.Component;

public abstract class RenderMarkerComp implements Component {
    
    private final int hash;
    
    public RenderMarkerComp() {
        this.hash = System.identityHashCode(this);
    }
    
    @Override
    public int hashCode() {
        return hash;
    }
}
