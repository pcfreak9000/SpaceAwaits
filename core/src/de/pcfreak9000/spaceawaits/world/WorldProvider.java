package de.pcfreak9000.spaceawaits.world;

import java.util.function.Consumer;

import de.pcfreak9000.spaceawaits.world.light.AmbientLightProvider;
import de.pcfreak9000.spaceawaits.world.tile.Chunk;

public interface WorldProvider {
    
    Background getBackground();//TMP?
    
    AmbientLightProvider getAmbientLight();
    
    WorldMeta getMeta();
    
    void requestChunk(int gcx, int gcy, Consumer<Chunk> onChunkLoaded);
    
    void unload(Chunk c);
}
