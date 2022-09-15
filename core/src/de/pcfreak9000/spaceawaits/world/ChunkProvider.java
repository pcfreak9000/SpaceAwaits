package de.pcfreak9000.spaceawaits.world;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import com.badlogic.gdx.utils.Queue;

import de.pcfreak9000.spaceawaits.util.Direction;
import de.pcfreak9000.spaceawaits.util.IntCoordKey;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk.ChunkGenStage;
import de.pcfreak9000.spaceawaits.world.gen.IChunkGenerator;

public class ChunkProvider implements IChunkProvider {
    
    private World world;
    private IChunkLoader loader;
    private IChunkGenerator chunkGen;
    
    private Chunk cached = null;
    
    private Queue<IntCoordKey> chunkUsagePrioQueue = new Queue<>();
    
    private int populationModeLevel = 0;
    private Map<IntCoordKey, Chunk> chunks = new HashMap<>();
    private Map<IntCoordKey, Chunk> genChunks = new HashMap<>();
    
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
        if (chunkUsagePrioQueue.size > 152) {
            while (chunkUsagePrioQueue.size > 145) {
                IntCoordKey k = chunkUsagePrioQueue.removeFirst();
                Chunk toUnload = chunks.remove(k);
                if (toUnload.isActive()) {
                    world.removeChunk(toUnload);
                }
                loader.unloadChunk(toUnload);
            }
            invalidateCache();
        }
    }
    
    private Chunk ensureChunk(int x, int y, ChunkGenStage stage) {
        if (stage == null || stage == ChunkGenStage.Empty)
            return null;
        if (!world.getBounds().inBoundsChunk(x, y))
            return null;
        IntCoordKey key = new IntCoordKey(x, y);
        Chunk chunk = chunks.get(key);
        //Recently used chunks are prioritized higher (meaning they are unloaded later)
        if (chunk != null && !isTmpMode()) {
            if (!Objects.equals(key, chunkUsagePrioQueue.last())) {
                chunkUsagePrioQueue.removeValue(key, false);
                chunkUsagePrioQueue.addLast(key);
                checkQueue();
            }
        }
        if (chunk == null) {
            if (isTmpMode()) {
                chunk = genChunks.get(key);
            }
            if (chunk == null) {
                chunk = loader.loadChunk(key);
            }
            if (isTmpMode()) {
                genChunks.put(key, chunk);
            } else {
                chunks.put(key, chunk);
                chunkUsagePrioQueue.removeValue(key, false);
                chunkUsagePrioQueue.addLast(key);
                checkQueue();
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
                //cached isn't moved up in the usage queue...
                return cached;
            }
        }
        Chunk chunk = ensureChunk(x, y, ChunkGenStage.Populated);
        // if (chunk == null) {  
        //Why here? Before the population??
        //            if (!populationMode && active) {
        //                if (!chunk.isActive()) {
        //                    world.addChunk(chunk);
        //                }
        //            }
        
        //}
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
            if (!chunks.containsKey(k)) {
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
        for (Chunk c : chunks.values()) {
            if (c.isActive()) {
                world.removeChunk(c);
            }
            this.loader.unloadChunk(c);
        }
        this.chunks.clear();
        this.chunkUsagePrioQueue.clear();
    }
    
    @Override
    public int getLoadedChunkCount() {
        return chunks.size();
    }
    
    public void saveAll() {
        for (Chunk c : chunks.values()) {
            this.loader.saveChunk(c);
        }
    }
}
