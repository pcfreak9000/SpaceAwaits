package de.pcfreak9000.spaceawaits.world;

import de.pcfreak9000.spaceawaits.util.IntCoords;

public interface ITicket {
    
    void update(World world, float dt);
    
    boolean isValid();
    
    IntCoords[] getLoadChunks();
    
}
