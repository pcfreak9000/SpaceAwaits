package de.pcfreak9000.spaceawaits.tileworld.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class TickRegionSystem extends IteratingSystem {
    
    private ComponentMapper<TickRegionComponent> tMapper = ComponentMapper.getFor(TickRegionComponent.class);
    
    public TickRegionSystem() {
        super(Family.all(TickRegionComponent.class).get());
        
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TickRegionComponent c = tMapper.get(entity);
        c.region.tick(deltaTime);
    }
    
}
