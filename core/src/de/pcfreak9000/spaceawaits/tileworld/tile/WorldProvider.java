package de.pcfreak9000.spaceawaits.tileworld.tile;

import java.util.function.Consumer;

import de.pcfreak9000.spaceawaits.tileworld.Background;
import de.pcfreak9000.spaceawaits.tileworld.light.AmbientLightProvider;

public interface WorldProvider {
    
    Background getBackground();//TMP?
    
    AmbientLightProvider getAmbientLight();
    
    WorldMeta getMeta();
    
    void requestChunk(int gcx, int gcy, Consumer<Chunk> onChunkLoaded);
    
    void unload(Chunk c);
}
