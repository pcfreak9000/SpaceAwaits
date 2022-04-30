package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.EntitySystem;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldEvents;

public class StructureManager extends EntitySystem {
    
    private NBTCompound storage = new NBTCompound();
    
    public StructureManager(World world) {
        world.getWorldBus().register(this);
    }
    
    @EventSubscription
    private void metanbtev(WorldEvents.WMNBTReadingEvent ev) {
        this.storage = ev.worldMetaNbt.getCompound("structure");
    }
    
    @EventSubscription
    private void metanbtev2(WorldEvents.WMNBTWritingEvent ev) {
        ev.worldMetaNbt.putCompound("structure", storage);
    }
}
