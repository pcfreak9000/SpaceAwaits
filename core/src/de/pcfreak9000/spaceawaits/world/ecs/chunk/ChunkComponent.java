package de.pcfreak9000.spaceawaits.world.ecs.chunk;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.world.tile.Chunk;

public class ChunkComponent implements Component {
    
    public final Chunk chunk;
    
    public ChunkComponent(Chunk r) {
        this.chunk = r;
    }
}
