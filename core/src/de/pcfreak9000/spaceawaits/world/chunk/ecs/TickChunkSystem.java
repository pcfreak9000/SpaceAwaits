package de.pcfreak9000.spaceawaits.world.chunk.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class TickChunkSystem extends IteratingSystem {
    
    private ComponentMapper<ChunkComponent> tMapper = ComponentMapper.getFor(ChunkComponent.class);
        
    public TickChunkSystem() {
        super(Family.all(ChunkComponent.class).get());    
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ChunkComponent c = tMapper.get(entity);
        c.chunk.tick(deltaTime);
    }
    
}
