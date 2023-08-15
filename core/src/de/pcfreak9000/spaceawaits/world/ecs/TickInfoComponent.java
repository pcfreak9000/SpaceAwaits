package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Component;

public class TickInfoComponent implements Component {
    
    long tick;
    
    public long getTick() {
        return tick;
    }
    
}
