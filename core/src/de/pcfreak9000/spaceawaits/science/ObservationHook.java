package de.pcfreak9000.spaceawaits.science;

import com.badlogic.ashley.core.Engine;

public abstract class ObservationHook {
    protected final Science science;
    protected final Observation observation;
    
    public ObservationHook(Observation obs, Science science) {
        this.science = science;
        this.observation = obs;
    }
    
    public boolean isDynamic() {
        return false;
    }
    
    public void update(Engine engine, float deltaTime) {
    }
    
}
