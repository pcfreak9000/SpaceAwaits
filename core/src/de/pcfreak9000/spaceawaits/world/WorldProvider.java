package de.pcfreak9000.spaceawaits.world;

import java.util.function.Consumer;

import de.pcfreak9000.spaceawaits.world.tile.Chunk;

public interface WorldProvider {
    
    Global getGlobal();
    
    WorldMeta getMeta();
    
    void requestChunk(int gcx, int gcy, Consumer<Chunk> onChunkLoaded);
    
    void unload(Chunk c);
}
