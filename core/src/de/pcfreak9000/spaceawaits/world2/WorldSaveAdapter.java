package de.pcfreak9000.spaceawaits.world2;

import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.world.tile.Chunk;

public class WorldSaveAdapter {
    private IWorldSave save;
    
    public boolean hasChunk(int x, int y) {
        return save.hasChunk(x, y);
    }
    
    public void loadChunk(Chunk c) {
        
    }
}
