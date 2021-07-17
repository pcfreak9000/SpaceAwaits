package de.pcfreak9000.spaceawaits.world;

import java.util.LinkedHashSet;
import java.util.Set;

import de.pcfreak9000.spaceawaits.world.tile.Chunk;

public class ProbeChunkManager {
    
    private ChunkProvider chunkProvider;
    private World world;
    private Set<Chunk> chunksLoaded = new LinkedHashSet<>();
    private Set<Chunk> formerInactiveChunks = new LinkedHashSet<>();
    
    public ProbeChunkManager(ChunkProvider provider, World world) {
        this.chunkProvider = provider;
        this.world = world;
    }
    
    public Chunk loadChunk(int x, int y, boolean activate) {
        if (!world.getBounds().inChunkBounds(x, y)) {
            return null;
        }
        boolean wasLoaded = chunkProvider.isLoaded(x, y);
        Chunk c = chunkProvider.loadChunk(x, y);
        if (!wasLoaded) {
            chunksLoaded.add(c);
        }
        if (!c.isActive() && activate) {
            formerInactiveChunks.add(c);
            world.addChunk(c);
        }
        return c;
    }
    
    public Chunk getChunk(int x, int y) {
        return chunkProvider.getChunk(x, y);
    }
    
    public int countExtraChunks() {
        return chunksLoaded.size();
    }
    
    public void unloadExtraChunks() {
        for (Chunk c : formerInactiveChunks) {
            world.removeChunk(c);
        }
        formerInactiveChunks.clear();
        for (Chunk k : chunksLoaded) {
            chunkProvider.queueUnloadChunk(k.getGlobalChunkX(), k.getGlobalChunkY());
        }
        chunksLoaded.clear();
        chunkProvider.unloadQueued();
    }
}
