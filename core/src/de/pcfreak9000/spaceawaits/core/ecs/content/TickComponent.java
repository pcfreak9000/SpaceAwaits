package de.pcfreak9000.spaceawaits.core.ecs.content;

import com.badlogic.ashley.core.Component;

public class TickComponent implements Component {
    
    public final Tickable tickable;
    
    public TickComponent(Tickable r) {
        this.tickable = r;
    }
}
