package de.pcfreak9000.spaceawaits.world;

import de.pcfreak9000.spaceawaits.util.IntCoordKey;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

public interface IChunkLoader {
    
    void unloadChunk(Chunk c);
    
    void saveChunk(Chunk c);
    
    Chunk loadChunk(IntCoordKey key);
    
}
