package de.pcfreak9000.spaceawaits.tileworld.ecs;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.tileworld.tile.Region;

public class RegionComponent implements Component {
    
    public final Region region;
    
    public RegionComponent(Region r) {
        this.region = r;
    }
}
