package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class BreakingSystem extends IteratingSystem {
    
    public BreakingSystem() {
        super(Family.all(BreakingComponent.class).get());
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BreakingComponent bc = Components.BREAKING.get(entity);
        if (bc.last == bc.progress) {
            entity.remove(BreakingComponent.class);
            return;
        }
        
    }
}
