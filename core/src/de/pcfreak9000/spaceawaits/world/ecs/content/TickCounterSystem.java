package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.EntitySystem;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.WorldEvents.WorldMetaNBTEvent.Type;
//Maybe this whole system is just a waste, so small
//Also, this isn't the proper ECS way of doing stuff but creating one entity with a tickcomponent and then retrieving that somewhere else is pretty ugly and stupid
public class TickCounterSystem extends EntitySystem {
    
    private long tick;
    
    public TickCounterSystem(World world) {
        world.getWorldBus().register(this);
    }
    
    @Override
    public void update(float deltaTime) {
        tick++;
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
