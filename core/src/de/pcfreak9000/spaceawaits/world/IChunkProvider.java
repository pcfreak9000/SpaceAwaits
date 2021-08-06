package de.pcfreak9000.spaceawaits.world;

import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

public interface IChunkProvider {
    
    int loadedChunkCount();
    
    //When should the below return null? Should we create some sort of empty chunk instead?
    
    Chunk getChunk(int x, int y);
    
    //Chunk loadChunk(int x, int y); //- server only???
    
    void queueUnloadChunk(int x, int y);
    
    //void saveAll(); //- server only???
    
    void queueUnloadAll();
    
    void unloadQueued();
}
