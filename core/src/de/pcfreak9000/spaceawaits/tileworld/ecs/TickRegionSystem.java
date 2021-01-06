package de.pcfreak9000.spaceawaits.tileworld.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class TickRegionSystem extends IteratingSystem {
    
    private ComponentMapper<RegionComponent> tMapper = ComponentMapper.getFor(RegionComponent.class);
        
    public TickRegionSystem() {
        super(Family.all(RegionComponent.class).get());    
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        RegionComponent c = tMapper.get(entity);
        c.chunk.tick(deltaTime);
    }
    
}
