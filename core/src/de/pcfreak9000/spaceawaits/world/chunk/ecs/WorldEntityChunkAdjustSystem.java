package de.pcfreak9000.spaceawaits.world.chunk.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;

public class WorldEntityChunkAdjustSystem extends IteratingSystem {
    
    private World world;
    
    public WorldEntityChunkAdjustSystem(World world) {
        super(Family.all(ChunkComponent.class, TransformComponent.class).get());
        this.world = world;
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent tc = Components.TRANSFORM.get(entity);
        ChunkComponent mwec = Components.CHUNK.get(entity);
        this.world.adjustChunk(entity, mwec, tc);
    }
}
