package de.pcfreak9000.spaceawaits.world;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.badlogic.gdx.utils.Queue;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import de.pcfreak9000.spaceawaits.util.Direction;
import de.pcfreak9000.spaceawaits.util.IntCoordKey;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk.ChunkGenStage;
import de.pcfreak9000.spaceawaits.world.gen.IChunkGenerator;

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
    
    private Chunk cached = null;
    
    private Queue<IntCoordKey> extra = new Queue<>();
    
    private boolean populationMode = false;
    private Map<IntCoordKey, Chunk> chunksNew = new HashMap<>();
    private Map<IntCoordKey, Chunk> genChunks = new HashMap<>();
    
    private IChunkGenerator chunkGen;
    
    public ChunkProvider(World world, IChunkLoader loader, IChunkGenerator chunkGen) {
        this.world = world;
        this.loader = loader;
        this.chunkGen = chunkGen;
    }
    
    @Override
    public Chunk getChunk(int x, int y) {
        return getChunk(x, y, false);
    }
    
    private void checkQueue() {
        if (extra.size > 150) {
            while (extra.size > 145) {
                IntCoordKey k = extra.removeFirst();
                Chunk toUnload = chunksNew.remove(k);
                if (toUnload.isActive()) {
                    world.removeChunk(toUnload);
                }
                loader.unloadChunk(toUnload);
            }
            invalidateCache();
        }
    }
    
    public Chunk getChunk(int x, int y, boolean active) {
        if (!world.getBounds().inBoundsChunk(x, y))
            return null;
        if (cached != null) {
            if (cached.getGlobalChunkX() == x && cached.getGlobalChunkY() == y) {
                return cached;
            }
        }
        IntCoordKey key = new IntCoordKey(x, y);
        Chunk chunk = chunksNew.get(key);
        //Recently used chunks are prioritized higher (meaning they are unloaded later)
        if (chunk != null && !populationMode) {
            if (!Objects.equals(key, extra.last())) {
                extra.removeValue(key, false);
                extra.addLast(key);
                checkQueue();
            }
        }
        if (chunk == null && populationMode) {
            chunk = genChunks.get(key);
        }
        if (chunk == null) {
            chunk = loader.loadChunk(key);
            if (populationMode) {
                genChunks.put(key, chunk);
            } else {
                chunksNew.put(key, chunk);
                extra.removeValue(key, false);
                extra.addLast(key);
                checkQueue();
            }
            if (!populationMode && active) {
                if (!chunk.isActive()) {
                    world.addChunk(chunk);
                }
            }
            if (chunk.getGenStage() == ChunkGenStage.Generated && !populationMode) {
                populationMode = true;
                for (Direction d : Direction.MOORE_NEIGHBOURS) {
                    int nx = d.dx + key.getX();
                    int ny = d.dy + key.getY();
                    getChunk(nx, ny, false);
                }
                chunk.populate(chunkGen);
                Iterator<IntCoordKey> it = genChunks.keySet().iterator();
                while (it.hasNext()) {
                    IntCoordKey k = it.next();
                    Chunk toRem = genChunks.get(k);
                    if (!chunksNew.containsKey(k)) {
                        loader.unloadChunk(toRem);
                    }
                    it.remove();
                }
                invalidateCache();
                populationMode = false;
            }
        }
        if (active && !chunk.isActive()) {
            world.addChunk(chunk);
        }
        cached = chunk;
        return chunk;
    }
    
    //    public void requireChunk(int x, int y, boolean active, Object lock) {
    //        Objects.requireNonNull(lock);
    //        if (!world.getBounds().inBoundsChunk(x, y))
    //            return;
    //        IntCoordKey key = new IntCoordKey(x, y);
    //        ChunkData cd = chunks.get(key);
    //        if (cd == null) {
    //            cd = new ChunkData();
    //            chunks.put(key, cd);
    //            cd.c = loader.loadChunk(key);
    //        }
    //        Chunk c = cd.c;
    //        chunkStuff.put(lock, key);
    //        if (active) {
    //            cd.requiredActive.add(lock);
    //            cd.required.remove(lock);
    //            if (!c.isActive()) {
    //                world.addChunk(c);
    //            }
    //        } else {
    //            cd.required.add(lock);
    //            cd.requiredActive.remove(lock);
    //            if (c.isActive()) {
    //                world.removeChunk(c);
    //            }
    //        }
    //    }
    //    
    //    public void releaseChunk(int x, int y, Object lock) {
    //        Objects.requireNonNull(lock);
    //        if (!world.getBounds().inBoundsChunk(x, y))
    //            return;
    //        IntCoordKey key = new IntCoordKey(x, y);
    //        ChunkData cd = chunks.get(key);
    //        if (cd == null)
    //            return;
    //        Chunk c = cd.c;
    //        chunkStuff.remove(lock, key);
    //        cd.required.remove(lock);
    //        cd.requiredActive.remove(lock);
    //        if (c.isActive()) {
    //            world.removeChunk(c);
    //        }
    //        if (cd.required.isEmpty() && cd.requiredActive.isEmpty()) {
    //            chunks.remove(key);
    //            if (cached == c) {
    //                invalidateCache();
    //            }
    //            loader.unloadChunk(c);
    //        }
    //    }
    //    
    //    public void releaseLock(Object lock) {
    //        Objects.requireNonNull(lock);
    //        Set<IntCoordKey> keys = new LinkedHashSet<>(chunkStuff.get(lock));
    //        for (IntCoordKey key : keys) {
    //            releaseChunk(key.getX(), key.getY(), lock);
    //        }
    //    }
    //    
    //    public int getLoadedChunkCountLock(Object lock) {
    //        return chunkStuff.get(lock).size();
    //    }
    
    private void invalidateCache() {
        cached = null;
    }
    
    public void unloadAll() {
        invalidateCache();
        for (Chunk c : chunksNew.values()) {
            if (c.isActive()) {
                world.removeChunk(c);
            }
            this.loader.unloadChunk(c);
        }
        this.chunksNew.clear();
        this.extra.clear();
    }
    
    //    public void releaseAll() {
    //        invalidateCache();
    //        for (ChunkData cd : chunks.values()) {
    //            if (cd.c.isActive()) {
    //                world.removeChunk(cd.c);
    //            }
    //            loader.unloadChunk(cd.c);
    //        }
    //        chunks.clear();
    //        chunkStuff.clear();
    //        extra.clear();
    //    }
    
    //    @Override
    //    public Chunk getChunk(int x, int y) {
    //        if (!world.getBounds().inBoundsChunk(x, y))
    //            return null;
    //        if (cached != null) {
    //            if (cached.getGlobalChunkX() == x && cached.getGlobalChunkY() == y) {
    //                return cached;
    //            }
    //        }
    //        IntCoordKey key = new IntCoordKey(x, y);
    //        ChunkData cd = chunks.get(key);
    //        if (cd != null) {
    //            cached = cd.c;
    //            extra.removeValue(key, false);
    //            extra.addLast(key);
    //            return cd.c;
    //        }
    //        requireChunk(key.getX(), key.getY(), true, this);//active chunks could lead to weird stuff, but for testing against physics bodies...
    //        extra.addLast(key);
    //        //Thread.dumpStack();
    //        //System.out.println("Extra Chunk!");
    //        if (extra.size > 6) {
    //            IntCoordKey k = extra.removeFirst();
    //            releaseChunk(k.getX(), k.getY(), this);
    //        }
    //        return getChunk(x, y);
    //    }
    
    @Override
    public int getLoadedChunkCount() {
        return chunksNew.size();
    }
    
    public void saveAll() {
        for (Chunk c : chunksNew.values()) {
            this.loader.saveChunk(c);
        }
        //        for (ChunkData cd : chunks.values()) {
        //            this.loader.saveChunk(cd.c);
        //        }
    }
}
