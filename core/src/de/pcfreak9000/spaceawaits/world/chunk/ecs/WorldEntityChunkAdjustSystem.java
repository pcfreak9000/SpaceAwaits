package de.pcfreak9000.spaceawaits.world.chunk.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;

public class WorldEntityChunkAdjustSystem extends IteratingSystem {
    
    private final ComponentMapper<ChunkMarkerComponent> mwecMapper = ComponentMapper.getFor(ChunkMarkerComponent.class);
    
    private final ComponentMapper<TransformComponent> transformMapper = ComponentMapper
            .getFor(TransformComponent.class);
    
    private World world;
    
    public WorldEntityChunkAdjustSystem(World world) {
        super(Family.all(ChunkMarkerComponent.class, TransformComponent.class).get());
        this.world = world;
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent tc = transformMapper.get(entity);
        ChunkMarkerComponent mwec = mwecMapper.get(entity);
        this.world.adjustChunk(entity, mwec, tc);
    }
}
