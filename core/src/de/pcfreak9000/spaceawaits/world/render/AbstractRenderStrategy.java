package de.pcfreak9000.spaceawaits.world.render;

import java.util.Objects;

import com.badlogic.ashley.core.Family;

public abstract class AbstractRenderStrategy implements IRenderDecorator {
    
    private final Family family;
    
    public AbstractRenderStrategy(Family family) {
        this.family = Objects.requireNonNull(family);
    }
    
    @Override
    public Family getFamily() {
        return family;
    }
    
}
