package de.pcfreak9000.spaceawaits.world;

import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.gdx.utils.IntArray;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import de.pcfreak9000.spaceawaits.util.IntCoordKey;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

public class ChunkProvider implements IChunkProvider {//TODO implement IChunkProvider and clean it up
    private static int max(IntArray array) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < array.size; i++) {
            max = Math.max(max, array.items[i]);
        }
        return max;
    }
    
    public static class ChunkData {
        private Chunk chunk;
        private int levelActual;
        private IntArray levels = new IntArray();
        
        public Chunk getChunk() {
            return chunk;
        }
        
        public int getLevel() {
            return levelActual;
        }
    }
    
    public static final int LOWEST_ACTIVE_LEVEL = 3;
    
    private Map<IntCoordKey, ChunkData> chunks = new LinkedHashMap<>();
    private Multimap<Object, int[]> sources = ArrayListMultimap.create();
    private IChunkLoader chunkLoader;
    private World world;
    
    public ChunkProvider(World world, IChunkLoader icl) {
        this.world = world;
        this.chunkLoader = icl;
    }
    
    public ChunkData get(int x, int y) {
        if (!world.getBounds().inChunkBounds(x, y))
            return null;
        IntCoordKey key = new IntCoordKey(x, y);
        ChunkData d = chunks.get(key);
        return d;
    }
    
    @Override
    public Chunk getChunk(int x, int y) {
        ChunkData cd = get(x, y);
        return cd == null ? null : cd.chunk;
    }
    
    public void saveAll() {
        for (ChunkData cd : chunks.values()) {
            this.chunkLoader.saveChunk(cd.chunk);
        }
    }
    
    public void save(int x, int y) {
        if (!world.getBounds().inChunkBounds(x, y))
            return;
        IntCoordKey key = new IntCoordKey(x, y);
        ChunkData cd = chunks.get(key);
        if (cd != null) {
            this.chunkLoader.saveChunk(cd.chunk);
        }
    }
    
    public void removeLevel(int x, int y, int level) {
        if (!world.getBounds().inChunkBounds(x, y))
            return;
        IntCoordKey key = new IntCoordKey(x, y);
        ChunkData d = chunks.get(key);
        if (d == null)
            return;
        boolean b = d.levels.removeValue(level);
        if (!b)
            return;
        if (d.levels.isEmpty()) {
            chunks.remove(key);
            Chunk c = d.chunk;
            if (c.isActive()) {
                world.removeChunk(c);
            }
            //save chunk, schedule unloading, whatever
            chunkLoader.unloadChunk(c);
        } else {
            d.levelActual = max(d.levels);
            if (d.levelActual < LOWEST_ACTIVE_LEVEL && d.chunk.isActive()) {
                world.removeChunk(d.chunk);
            }
        }
    }
    
    public void addLevel(int x, int y, int level, Object src) {
        if (!world.getBounds().inChunkBounds(x, y))
            return;
        IntCoordKey key = new IntCoordKey(x, y);
        ChunkData d = chunks.get(key);
        if (d == null) {
            if (!this.chunkLoader.canLoad(key)) {
                return;
            }
            d = new ChunkData();
            chunks.put(key, d);
            //load chunk
            d.chunk = chunkLoader.loadChunk(key);
        }
        d.levels.add(level);
        d.levelActual = level > d.levelActual ? level : max(d.levels);
        if (d.levelActual >= LOWEST_ACTIVE_LEVEL && !d.chunk.isActive()) {
            world.addChunk(d.chunk);
        }
        if (src != null) {
            sources.put(src, new int[] { x, y, level });
        }
    }
    
    public int getChunkCount() {
        return chunks.size();
    }
    
    @Override
    public int loadedChunkCount() {
        return getChunkCount();
    }
    
    public int getSrcChunkCount(Object src) {
        return sources.get(src).size();
    }
    
    public void removeAllSrc(Object src) {
        for (int[] k : sources.get(src)) {
            removeLevel(k[0], k[1], k[2]);
        }
    }
}
