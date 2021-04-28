package de.pcfreak9000.spaceawaits.world2;

import de.pcfreak9000.spaceawaits.world.tile.Chunk;

public interface IChunkProvider {
    
    int loadedChunkCount();
    
    Chunk getChunk(int x, int y);
    
    //Chunk loadChunk(int x, int y); //- server only???
    
    void queueUnloadChunk(int x, int y);
    
    //void saveAll(); //- server only???
    
    void queueUnloadAll();
    
    void unloadQueued();
}
