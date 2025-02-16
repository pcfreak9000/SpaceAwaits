package de.pcfreak9000.spaceawaits.world.tile;

import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

public interface ChunkECSHandler {
    void addChunk(Chunk c);
    
    void removeChunk(Chunk c);
}
