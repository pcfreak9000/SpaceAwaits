package de.pcfreak9000.spaceawaits.tileworld.ecs;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.tileworld.tile.Chunk;

public class ChunkComponent implements Component {
    
    public final Chunk chunk;
    
    public ChunkComponent(Chunk r) {
        this.chunk = r;
    }
}
