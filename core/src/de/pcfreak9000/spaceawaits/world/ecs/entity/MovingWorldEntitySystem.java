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
        MovingWorldEntityComponent mwec = mwecMapper.get(entity);
        this.world.adjustChunk(entity, mwec, tc);
    }
}
