package de.pcfreak9000.spaceawaits.world.ecs.entity;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.WorldAccessor;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;

public class MovingWorldEntitySystem extends IteratingSystem {
    
    private final ComponentMapper<ChunkMarkerComponent> mwecMapper = ComponentMapper
            .getFor(ChunkMarkerComponent.class);
    
    private final ComponentMapper<TransformComponent> transformMapper = ComponentMapper
            .getFor(TransformComponent.class);
    
    private WorldAccessor world;
    
    public MovingWorldEntitySystem() {
        super(Family.all(ChunkMarkerComponent.class, TransformComponent.class).get());
        SpaceAwaits.BUS.register(this);
    }
    
    @EventSubscription
    public void onevent(WorldEvents.SetWorldEvent world) {
        this.world = world.worldMgr.getWorldAccess();
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent tc = transformMapper.get(entity);
        ChunkMarkerComponent mwec = mwecMapper.get(entity);
        this.world.adjustChunk(entity, mwec, tc);
    }
}
