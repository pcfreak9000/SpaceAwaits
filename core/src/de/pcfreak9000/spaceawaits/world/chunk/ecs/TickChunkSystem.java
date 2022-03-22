package de.pcfreak9000.spaceawaits.world.chunk.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.pcfreak9000.spaceawaits.world.ecs.SingleEntityCompRetriever;
import de.pcfreak9000.spaceawaits.world.ecs.content.TickInfoComponent;

public class TickChunkSystem extends IteratingSystem {
    
    private ComponentMapper<ChunkComponent> tMapper = ComponentMapper.getFor(ChunkComponent.class);
    
    private SingleEntityCompRetriever<TickInfoComponent> tickInfo = new SingleEntityCompRetriever<>(TickInfoComponent.class);
    
    public TickChunkSystem() {
        super(Family.all(ChunkComponent.class).get());
    }
    
    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        tickInfo.addedToEngine(engine);
    }
    
    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        tickInfo.removedFromEngine();
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ChunkComponent c = tMapper.get(entity);
        c.chunk.tick(deltaTime, tickInfo.get().getTick());
    }
    
}
