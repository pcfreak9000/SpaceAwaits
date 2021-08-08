package de.pcfreak9000.spaceawaits.world.chunk.ecs;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

public class ChunkComponent implements Component {
    
    public final Chunk chunk;
    
    public ChunkComponent(Chunk r) {
        this.chunk = r;
    }
}