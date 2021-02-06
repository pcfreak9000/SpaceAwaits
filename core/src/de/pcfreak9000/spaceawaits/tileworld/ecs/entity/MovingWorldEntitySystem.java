package de.pcfreak9000.spaceawaits.tileworld.ecs.entity;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.tileworld.WorldAccessor;
import de.pcfreak9000.spaceawaits.tileworld.WorldEvents;
import de.pcfreak9000.spaceawaits.tileworld.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.tileworld.tile.Chunk;
import de.pcfreak9000.spaceawaits.tileworld.tile.Tile;

public class MovingWorldEntitySystem extends IteratingSystem {
    
    private final ComponentMapper<MovingWorldEntityComponent> mwecMapper = ComponentMapper
            .getFor(MovingWorldEntityComponent.class);
    
    private final ComponentMapper<TransformComponent> transformMapper = ComponentMapper
            .getFor(TransformComponent.class);
    
    private WorldAccessor world;
    
    public MovingWorldEntitySystem() {
        super(Family.all(MovingWorldEntityComponent.class, TransformComponent.class).get());
        SpaceAwaits.BUS.register(this);
    }
    
    @EventSubscription
    public void onevent(WorldEvents.SetWorldEvent world) {
        this.world = world.worldMgr.getWorldAccess();
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {//This is buggy if an entity leaves the world (the chunks stuff becomes confused)
        TransformComponent tc = transformMapper.get(entity);
        int supposedChunkX = Chunk.toGlobalChunk(Tile.toGlobalTile(tc.position.x));
        int supposedChunkY = Chunk.toGlobalChunk(Tile.toGlobalTile(tc.position.y));
        MovingWorldEntityComponent mwec = mwecMapper.get(entity);
        if (mwec.currentChunk == null) {
            mwec.currentChunk = world.getChunk(supposedChunkX, supposedChunkY);
        } else if (supposedChunkX != mwec.currentChunk.getGlobalChunkX()
                || supposedChunkY != mwec.currentChunk.getGlobalChunkY()) {
            mwec.currentChunk.removeEntity(entity);
            Chunk newchunk = world.getChunk(supposedChunkX, supposedChunkY);
            if (newchunk != null) {
                mwec.currentChunk = newchunk;
                mwec.currentChunk.addEntity(entity);
            }
        }
    }
}
