package de.pcfreak9000.spaceawaits.tileworld.tile;

import java.util.function.Consumer;

public interface WorldProvider {
    
    WorldMeta getMeta();
    
    void requestChunk(int gcx, int gcy, Consumer<Chunk> onChunkLoaded);
    
    void unload(Chunk c);
}
