package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.pcfreak9000.spaceawaits.world.World;

public class BreakingSystem extends IteratingSystem {
    
    private World world;
    
    public BreakingSystem(World world) {
        super(Family.all(BreakingComponent.class, BreakableComponent.class).get());
        this.world = world;
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BreakingComponent bc = Components.BREAKING.get(entity);
        if (bc.addProgress <= 0f) {
            entity.remove(BreakingComponent.class);
            return;
        }
        bc.progress += bc.addProgress * deltaTime;
        bc.addProgress = 0;
        if (bc.progress >= 1f) {
            entity.remove(BreakingComponent.class);
            BreakableComponent breakable = Components.BREAKABLE.get(entity);
            if (breakable.validate(entity)) {
                breakable.entityBroken.onEntityBroken(this.world, entity);
            }
        }
    }
}
