package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.WorldEvents.WorldMetaNBTEvent.Type;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;

public class TickCounterSystem extends EntitySystem {
    
    private static final ComponentMapper<TickInfoComponent> MAPPER = ComponentMapper.getFor(TickInfoComponent.class);
    
    private final Entity entity;
    
    public TickCounterSystem(World world) {
        this.entity = createTickInfoEntity();
        world.getWorldBus().register(this);
    }
    
    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntity(entity);
    }
    
    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        engine.removeEntity(entity);
    }
    
    @Override
    public void update(float deltaTime) {
        MAPPER.get(entity).tick++;
    }
    
    public long getTick() {
        return MAPPER.get(entity).getTick();
    }
    
    @EventSubscription
    private void metanbtev(WorldEvents.WorldMetaNBTEvent ev) {
        if (ev.type == Type.Writing) {
            ev.worldMetaNbt.putLong("currentTick", MAPPER.get(entity).tick);
        } else if (ev.type == Type.Reading) {
            MAPPER.get(entity).tick = ev.worldMetaNbt.getLongOrDefault("currentTick", 0);
        }
    }
    
    private Entity createTickInfoEntity() {
        Entity e = new EntityImproved();
        e.add(new TickInfoComponent());
        return e;
    }
}
