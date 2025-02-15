package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.pcfreak9000.spaceawaits.core.ecs.content.TransformComponent;

public class MoveTestSystem extends IteratingSystem {
    public MoveTestSystem() {
        super(Family.all(TransformComponent.class, MoveTestComponent.class).get());
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Components.TRANSFORM.get(entity).position.x += 3 * deltaTime;
    }
}
