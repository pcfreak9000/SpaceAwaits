package de.pcfreak9000.spaceawaits.world.ecs;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
//in the future, this class could resolve dependencies between systems
public class SystemResolver {
    
    private List<EntitySystem> systems = new ArrayList<>();
    
    public void addSystem(EntitySystem system) {
        systems.add(system);
    }
    
    public void setupSystems(Engine ecs) {
        for (EntitySystem e : systems) {
            ecs.addSystem(e);
        }
    }
    
}
