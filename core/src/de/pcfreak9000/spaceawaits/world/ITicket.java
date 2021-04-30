package de.pcfreak9000.spaceawaits.world;

public interface ITicket {
    
    void update(World world, float dt);
    
    boolean isValid();
    
    ChunkCoordinates[] getLoadChunks();
    
}
