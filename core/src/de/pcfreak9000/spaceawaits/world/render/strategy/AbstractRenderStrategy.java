package de.pcfreak9000.spaceawaits.world.render.strategy;

import java.util.Objects;

import com.badlogic.ashley.core.Family;

public abstract class AbstractRenderStrategy implements IRenderStrategy {
    
    private final Family family;
    
    public AbstractRenderStrategy(Family family) {
        this.family = Objects.requireNonNull(family);
    }
    
    @Override
    public Family getFamily() {
        return family;
    }
    
}
