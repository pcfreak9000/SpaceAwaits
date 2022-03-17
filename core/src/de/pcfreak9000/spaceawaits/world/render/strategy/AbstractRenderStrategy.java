package de.pcfreak9000.spaceawaits.world.render.strategy;

import java.util.Objects;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Family;

public abstract class AbstractRenderStrategy implements IRenderStrategy {
    
    private final Family family;
    private Engine engine;
    
    public AbstractRenderStrategy(Family family) {
        this.family = Objects.requireNonNull(family);
    }
    
    protected void addedToEngine(Engine engine) {
        
    }
    
    protected void removedFromEngine(Engine engine) {
        
    }
    
    public final void addedToEngineInternal(Engine engine) {
        this.engine = engine;
        addedToEngine(engine);
    }
    
    public final void removedFromEngineInternal(Engine engine) {
        removedFromEngine(engine);
        this.engine = null;
    }
    
    @Override
    public Family getFamily() {
        return family;
    }
    
    protected Engine getEngine() {
        return engine;
    }
    
}
