package de.pcfreak9000.spaceawaits.science;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.core.ecs.EngineImproved;
import de.pcfreak9000.spaceawaits.core.ecs.Transferable;

//Not all of this needs to run every frame? -> IntervalEntitySystem
public class ScienceHookSystem extends EntitySystem implements Transferable {
    
    private Array<ObservationHook> activeHooks = new Array<>();
    private Array<ObservationHook> dynamicHooks = new Array<>();
    
    private final int envId;
    private Science science;
    
    public ScienceHookSystem(int envid, Science science) {
        this.envId = envid;
        this.science = science;
    }
    
    @Override
    public void load() {
        EngineImproved ei = (EngineImproved) getEngine();
        for (Observation o : Science.OBSERVATION_REGISTRY.getAll()) {
            if (science.isUnlocked(o)) {
                continue;
            }
            ObservationHook oh = o.createHook(envId, getEngine(), science);
            if (oh != null) {
                activeHooks.add(oh);
                ei.getEventBus().register(oh);
                if (oh.isDynamic()) {
                    dynamicHooks.add(oh);
                }
            }
        }
    }
    
    @Override
    public void update(float deltaTime) {
        for (ObservationHook oh : dynamicHooks) {
            oh.update(getEngine(), deltaTime);
        }
    }
    
    @Override
    public void unload() {
        EngineImproved ei = (EngineImproved) getEngine();
        for (ObservationHook oh : activeHooks) {
            ei.getEventBus().unregister(oh);
        }
    }
    
}
