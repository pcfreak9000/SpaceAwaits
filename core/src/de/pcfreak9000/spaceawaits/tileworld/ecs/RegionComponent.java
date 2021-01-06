package de.pcfreak9000.spaceawaits.tileworld.ecs;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.tileworld.tile.Chunk;

public class RegionComponent implements Component {
    
    public final Chunk chunk;
    
    public RegionComponent(Chunk r) {
        this.chunk = r;
    }
}
