package de.pcfreak9000.spaceawaits.world.chunk.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.pcfreak9000.spaceawaits.world.ecs.SystemCache;
import de.pcfreak9000.spaceawaits.world.ecs.content.TickCounterSystem;

public class TickChunkSystem extends IteratingSystem {
    
    private ComponentMapper<ChunkComponent> tMapper = ComponentMapper.getFor(ChunkComponent.class);
    
    private SystemCache<TickCounterSystem> ticks = new SystemCache<>(TickCounterSystem.class);
    
    public TickChunkSystem() {
        super(Family.all(ChunkComponent.class).get());
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ChunkComponent c = tMapper.get(entity);
        c.chunk.tick(deltaTime, ticks.get(getEngine()).getTick());
    }
    
}
