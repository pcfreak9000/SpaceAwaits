package de.pcfreak9000.spaceawaits.tileworld.ecs;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.tileworld.Region;

public class TickRegionComponent implements Component {
    
    public final Region region;
    
    public TickRegionComponent(Region r) {
        this.region = r;
    }
}
