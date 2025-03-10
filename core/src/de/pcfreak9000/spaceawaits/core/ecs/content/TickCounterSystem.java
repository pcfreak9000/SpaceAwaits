package de.pcfreak9000.spaceawaits.core.ecs.content;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.WorldEvents.WorldMetaNBTEvent.Type;
import de.pcfreak9000.spaceawaits.world.ecs.Components;

//Maybe this whole system is just a waste, so small
//Also, this isn't the proper ECS way of doing stuff but creating one entity with a tickcomponent and then retrieving that somewhere else is pretty ugly and stupid
public class TickCounterSystem extends IteratingSystem {
    
    private long tick;
    
    public TickCounterSystem() {
        super(Family.all(TickComponent.class).get());
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
    
    //TODO move into entity??
    @EventSubscription
    private void metanbtev(WorldEvents.WorldMetaNBTEvent ev) {
        if (ev.type == Type.Writing) {
            ev.worldMetaNbt.putLong("currentTick", tick);
        } else if (ev.type == Type.Reading) {
            tick = ev.worldMetaNbt.getLongOrDefault("currentTick", 0);
        }
    }
}
