package de.pcfreak9000.spaceawaits.world.chunk.mgmt;

import de.pcfreak9000.spaceawaits.util.IntCoords;

public interface ITicket {
    
    void update();
    
    boolean isValid();
    
    IntCoords[] getLoadChunks();
    
}
