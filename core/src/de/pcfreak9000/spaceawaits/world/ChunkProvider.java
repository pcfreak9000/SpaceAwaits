package de.pcfreak9000.spaceawaits.world;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import de.pcfreak9000.spaceawaits.util.IntCoordKey;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

public class ChunkProvider implements IChunkProvider {
    
    private static class ChunkData {
        private Set<Object> required = new HashSet<>();
        private Set<Object> requiredActive = new HashSet<>();
        private Chunk c;
    }
    
    private Multimap<Object, IntCoordKey> chunkStuff = MultimapBuilder.hashKeys().linkedHashSetValues().build();
    private Map<IntCoordKey, ChunkData> chunks = new HashMap<>();
    private World world;
    private IChunkLoader loader;
    
    public ChunkProvider(World world, IChunkLoader loader) {
        this.world = world;
        this.loader = loader;
    }
    
    public void requireChunk(int x, int y, boolean active, Object lock) {
        Objects.requireNonNull(lock);
        if (!world.getBounds().inChunkBounds(x, y))
            return;
        IntCoordKey key = new IntCoordKey(x, y);
        ChunkData cd = chunks.get(key);
        if (cd == null) {
            cd = new ChunkData();
            chunks.put(key, cd);
            cd.c = loader.loadChunk(key);
        }
        Chunk c = cd.c;
        chunkStuff.put(lock, key);
        if (active) {
            cd.requiredActive.add(lock);
            cd.required.remove(lock);
            if (!c.isActive()) {
                world.addChunk(c);
            }
        } else {
            cd.required.add(lock);
            cd.requiredActive.remove(lock);
            if (c.isActive()) {
                world.removeChunk(c);
            }
        }
    }
    
    public void releaseChunk(int x, int y, Object lock) {
        Objects.requireNonNull(lock);
        if (!world.getBounds().inChunkBounds(x, y))
            return;
        IntCoordKey key = new IntCoordKey(x, y);
        ChunkData cd = chunks.get(key);
        if (cd == null)
            return;
        Chunk c = cd.c;
        chunkStuff.remove(lock, key);
        cd.required.remove(lock);
        cd.requiredActive.remove(lock);
        if (c.isActive()) {
            world.removeChunk(c);
        }
        if (cd.required.isEmpty() && cd.requiredActive.isEmpty()) {
            chunks.remove(key);
            loader.unloadChunk(c);
        }
    }
    
    public void releaseLock(Object lock) {
        Objects.requireNonNull(lock);
        Set<IntCoordKey> keys = new LinkedHashSet<>(chunkStuff.get(lock));
        for (IntCoordKey key : keys) {
            releaseChunk(key.getX(), key.getY(), lock);
        }
    }
    
    public int getLoadedChunkCountLock(Object lock) {
        return chunkStuff.get(lock).size();
    }
    
    public void releaseAll() {
        for (ChunkData cd : chunks.values()) {
            if (cd.c.isActive()) {
                world.removeChunk(cd.c);
            }
            loader.unloadChunk(cd.c);
        }
        chunks.clear();
        chunkStuff.clear();
    }
    
    @Override
    public Chunk getChunk(int x, int y) {
        if (!world.getBounds().inChunkBounds(x, y))
            return null;
        IntCoordKey key = new IntCoordKey(x, y);
        ChunkData cd = chunks.get(key);
        return cd == null ? null : cd.c;
    }
    
    @Override
    public int getLoadedChunkCount() {
        return chunks.size();
    }
    
    public void saveAll() {
        for (ChunkData cd : chunks.values()) {
            this.loader.saveChunk(cd.c);
        }
    }
}
