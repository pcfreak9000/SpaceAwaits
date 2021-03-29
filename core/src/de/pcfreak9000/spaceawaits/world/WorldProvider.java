package de.pcfreak9000.spaceawaits.world;

import java.util.function.Consumer;

import de.pcfreak9000.spaceawaits.world.tile.Chunk;

public interface WorldProvider {
    
    Global requestGlobal();
    
    void unloadGlobal();
    
    WorldBounds getBounds();
    
    void requestChunk(int gcx, int gcy, Consumer<Chunk> onChunkLoaded);
    
    void unloadChunk(Chunk c);
}
