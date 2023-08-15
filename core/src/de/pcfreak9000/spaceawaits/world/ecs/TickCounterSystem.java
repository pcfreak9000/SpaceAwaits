package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.WorldEvents.WorldMetaNBTEvent.Type;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.TickComponent;

//Maybe this whole system is just a waste, so small
//Also, this isn't the proper ECS way of doing stuff but creating one entity with a tickcomponent and then retrieving that somewhere else is pretty ugly and stupid
public class TickCounterSystem extends IteratingSystem {
    
    private long tick;
    
    public TickCounterSystem(World world) {
        super(Family.all(TickComponent.class).get());
        world.getWorldBus().register(this);
    }
    
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        tick++;
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Components.TICK.get(entity).tickable.tick(deltaTime, tick);
    }
    
    public long getTick() {
        return tick;
    }
    
    @EventSubscription
    private void metanbtev(WorldEvents.WorldMetaNBTEvent ev) {
        if (ev.type == Type.Writing) {
            ev.worldMetaNbt.putLong("currentTick", tick);
        } else if (ev.type == Type.Reading) {
            tick = ev.worldMetaNbt.getLongOrDefault("currentTick", 0);
        }
    }
}
