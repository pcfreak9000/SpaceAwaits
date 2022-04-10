package de.pcfreak9000.spaceawaits.world.chunk.ecs;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.world.tile.Tickable;

public class TickComponent implements Component {
    
    public final Tickable tickable;
    
    public TickComponent(Tickable r) {
        this.tickable = r;
    }
}
