package de.pcfreak9000.spaceawaits.world2;

public interface ITicket {
    
    void update(float dt);
    
    boolean isValid();
    
    ChunkCoordinates[] getLoadChunks();
    
}
