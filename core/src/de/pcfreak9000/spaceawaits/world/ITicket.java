package de.pcfreak9000.spaceawaits.world;

public interface ITicket {
    
    void update(float dt);
    
    boolean isValid();
    
    ChunkCoordinates[] getLoadChunks();
    
}
