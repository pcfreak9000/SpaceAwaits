package de.pcfreak9000.spaceawaits.world.render;

import java.util.Objects;

import com.badlogic.ashley.core.Family;

public abstract class AbstractRenderDecorator implements IRenderDecorator {
    
    private final Family family;
    
    public AbstractRenderDecorator(Family family) {
        this.family = Objects.requireNonNull(family);
    }
    
    @Override
    public Family getFamily() {
        return family;
    }
    
}
