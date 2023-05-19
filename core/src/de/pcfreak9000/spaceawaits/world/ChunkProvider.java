package de.pcfreak9000.spaceawaits.world;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.pcfreak9000.spaceawaits.util.Direction;
import de.pcfreak9000.spaceawaits.util.IntCoordKey;
import de.pcfreak9000.spaceawaits.util.SpecialCache;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk.ChunkGenStage;
import de.pcfreak9000.spaceawaits.world.gen.IChunkGenerator;

public class ChunkProvider implements IChunkProvider {
    
    private World world;
    private IChunkLoader loader;
    private IChunkGenerator chunkGen;
    
    private Chunk cached = null;
    
    private int populationModeLevel = 0;
    private Map<IntCoordKey, Chunk> genChunks = new HashMap<>();
    
    private SpecialCache<IntCoordKey, Chunk> chunkCache;
    
    public ChunkProvider(World world, IChunkLoader loader, IChunkGenerator chunkGen) {
        this.world = world;
        this.loader = loader;
        this.chunkGen = chunkGen;
        this.chunkCache = new SpecialCache<>(152, 145, (key) -> loader.loadChunk(key), (chunk) -> {
            if (cached == chunk) {
                invalidateCache();
            }
            if (chunk.isActive()) {
                world.removeChunk(chunk);
            }
            loader.unloadChunk(chunk);
        });
    }
    
    @Override
    public Chunk getChunk(int x, int y) {
        return getChunk(x, y, false);
    }
    
    private Chunk ensureChunk(int x, int y, ChunkGenStage stage) {
        if (stage == null || stage == ChunkGenStage.Empty)
            return null;
        if (!world.getBounds().inBoundsChunk(x, y))
            return null;
        IntCoordKey key = new IntCoordKey(x, y);
        Chunk chunk = null;//chunks.get(key);
        if (!isTmpMode() || chunkCache.hasKey(key)) {
            chunk = chunkCache.getOrFresh(key);
        }
        if (isTmpMode() && chunk == null) {
            chunk = genChunks.get(key);
            if (chunk == null) {
                chunk = loader.loadChunk(key);
                genChunks.put(key, chunk);
            }
        }
        if (chunk.getGenStage().level >= stage.level) {
            return chunk;
        }
        populationModeLevel++;
        try {
            ensureChunk(x, y, stage.before);
            for (Direction d : Direction.MOORE_NEIGHBOURS) {
                int nx = d.dx + key.getX();
                int ny = d.dy + key.getY();
                ensureChunk(nx, ny, stage.before);
            }
            if (chunk.getGenStage() == ChunkGenStage.Empty && stage == ChunkGenStage.Tiled) {
                chunk.generate(chunkGen);
            } else if (chunk.getGenStage() == ChunkGenStage.Tiled && stage == ChunkGenStage.Structured) {
                chunk.structure(chunkGen);
            } else if (chunk.getGenStage() == ChunkGenStage.Structured && stage == ChunkGenStage.Populated) {
                chunk.populate(chunkGen);
            }
        } finally {
            populationModeLevel--;
            if (!isTmpMode()) {
                unloadTemporaryChunks();
                invalidateCache();
            }
        }
        return chunk;
    }
    
    public Chunk getChunk(int x, int y, boolean active) {
        if (!world.getBounds().inBoundsChunk(x, y))
            return null;
        if (cached != null) {
            if (cached.getGlobalChunkX() == x && cached.getGlobalChunkY() == y) {
                if (!cached.isActive() && active) {
                    world.addChunk(cached);
                }
                //cached isn't moved up in the usage queue... but the queue shouldn't have changed so cached should still be new
                return cached;
            }
        }
        if (isTmpMode()) {
            IntCoordKey key = new IntCoordKey(x, y);
            Chunk chunk = chunkCache.getFromCache(key);//chunks.get(key);
            if (chunk == null) {
                chunk = genChunks.get(key);
            }
            cached = chunk;
            return chunk;
        }
        Chunk chunk = ensureChunk(x, y, ChunkGenStage.Populated);
        if (active && !chunk.isActive()) {
            world.addChunk(chunk);
        }
        cached = chunk;
        return chunk;
    }
    
    private boolean isTmpMode() {
        return populationModeLevel > 0;
    }
    
    private void unloadTemporaryChunks() {
        Iterator<IntCoordKey> it = genChunks.keySet().iterator();
        while (it.hasNext()) {
            IntCoordKey k = it.next();
            Chunk toRem = genChunks.get(k);
            if (!chunkCache.hasKey(k)) {
                loader.unloadChunk(toRem);
            }
            it.remove();
        }
    }
    
    private void invalidateCache() {
        cached = null;
    }
    
    public void unloadAll() {
        unloadTemporaryChunks();
        invalidateCache();
        this.chunkCache.clear();
    }
    
    @Override
    public int getLoadedChunkCount() {
        return chunkCache.size();
    }
    
    public void saveAll() {
        for (Chunk c : chunkCache.values()) {
            this.loader.saveChunk(c);
        }
    }
}
