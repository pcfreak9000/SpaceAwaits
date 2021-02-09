package de.pcfreak9000.spaceawaits.world.ecs.chunk;

import com.badlogic.ashley.core.EntitySystem;

import de.pcfreak9000.spaceawaits.world.WorldAccessor;

public class ChunkReloadingSystem extends EntitySystem {
    
    private WorldAccessor world;
    
    public ChunkReloadingSystem(WorldAccessor world) {
        this.world = world;
    }
    
    @Override
    public void update(float deltaTime) {
        world.unloadload();
    }
}
