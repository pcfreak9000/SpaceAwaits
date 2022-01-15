package de.pcfreak9000.spaceawaits.world;

import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

public interface IChunkProvider {
    
    int getLoadedChunkCount();
    
    //When should the below return null? Should we create some sort of empty chunk instead?
    
    Chunk getChunk(int x, int y);
    
}
