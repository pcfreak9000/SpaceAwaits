package de.pcfreak9000.spaceawaits.world;

import com.badlogic.gdx.utils.LongMap;

import de.pcfreak9000.spaceawaits.util.Direction;
import de.pcfreak9000.spaceawaits.util.IntCoords;
import de.pcfreak9000.spaceawaits.util.SpecialCache2D;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk.ChunkGenStage;
import de.pcfreak9000.spaceawaits.world.gen.IChunkGenerator;

public class ChunkProvider implements IChunkProvider {
    
    private World world;
    private IChunkLoader loader;
    private IChunkGenerator chunkGen;
    
    private Chunk cached = null;
    
    private int populationModeLevel = 0;
    private LongMap<Chunk> genChunks = new LongMap<>();
    
    //SpecialCache2D would be better suited
    private SpecialCache2D<Chunk> chunkCache;
    
    public ChunkProvider(World world, IChunkLoader loader, IChunkGenerator chunkGen) {
        this.world = world;
        this.loader = loader;
        this.chunkGen = chunkGen;
        this.chunkCache = new SpecialCache2D<>(152, 145, (x, y) -> loader.loadChunk(x, y), (chunk) -> {
            if (cached == chunk) {
                invalidateCache();
            }
            if (chunk.isActive()) {
                world.removeChunk(chunk);
            }
            loader.unloadChunk(chunk);
        });
        //clear opposing tasks, or let them finish if necessary, then
        //submit -> run -> get, to prioritize the right now needed stuff. run runs it if it isnt already running.
        //get makes sure the result is available (if it ran on another thread run would be non blocking and the result might not yet be available)
        //then remove this stuff from the ResultQueue and add it or do whatever with it here
        
        //Results are placed in ConcurrentLinkedQueue, which adds (or whatever) its content at a certain point in the main thread
        //Results can have different actions? Adding it to the world, doing nothing, ...?
        //What about caching it and making it available for others?
        
        //requestChunk: x/y? now? active? genstage?
        //what happens if this function is not called on the main thread?? for example, the cache might indeed change because the mainthread is doing something
        //non-mainthread -> force now! doesn't solve all problems but might make things simpler...??
        //STILL! two non-mainthreads could require the same chunk to be generated at a certain genstage
        //population stage with world-patches, a singular thread for population if not currently in the mainthread, block if mainthread needs this, do on mainthread if active
        //population stage, force surrounding chunks to be a level below beforehand but now!
        //old tasks executed far into the future even though not needed anymore, what to do???
        //only tile chunks not involved in this pipeline anymore can be considered to be available, so the above comment has to wait for this
        //check if already requested/somewhere in the pipeline already:
        //0. Remove opposing actions, (finish blocking actions?)
        //1. check cached variable
        //2. check cache
        //3. check available results
        //4. check task queue, if found:
        //4a. if now, run and get
        //4b. if not now, do nothing/upgrade active/genstage
        //5. otherwise:
        //5a. if now, load/gen chunk
        //5b. if not now, create task
        //if genstage isn't reached by the chunk, upgrade it. If now, do it now, if not now, remove the chunk from any caches/queues and queue the upgrade task
        //only fully generated chunks should be active...
        //what about threaded access on the neighbouring chunk for generation? which might need this chunk in turn??
        
        //determine if a chunk can even be processed async or requires the main thread
        
        //somewhere note which chunk is currently used by which thread and in what way -> synchronizedMap?
        
        //cancelChunk, for chunks which are enqueued but not needed anymore??
        
        //Task with parentTask to wait for complete completion? Task: tasktype, parent
        
        //chunk.populate with IEntityArea or something with a "local" physics world maybe to check for collisions...
        
        //Timeout if something takes too long??
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
        long key = IntCoords.toLong(x, y);
        Chunk chunk = null;//chunks.get(key);
        if (!isTmpMode() || chunkCache.hasKey(x, y)) {
            chunk = chunkCache.getOrFresh(x, y);
        }
        if (isTmpMode() && chunk == null) {
            chunk = genChunks.get(key);
            if (chunk == null) {
                chunk = loader.loadChunk(x, y);
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
                int nx = d.dx + x;
                int ny = d.dy + y;
                ensureChunk(nx, ny, stage.before);
            }
            WorldArea area = new WorldArea(this, world.getBounds(), world);
            if (chunk.getGenStage() == ChunkGenStage.Empty && stage == ChunkGenStage.Tiled) {
                chunk.generate(chunkGen);
            } else if (chunk.getGenStage() == ChunkGenStage.Tiled && stage == ChunkGenStage.Structured) {
                chunk.structure(chunkGen, area);
            } else if (chunk.getGenStage() == ChunkGenStage.Structured && stage == ChunkGenStage.Populated) {
                chunk.populate(chunkGen, area);
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
            Chunk chunk = chunkCache.getFromCache(x, y);//chunks.get(key);
            if (chunk == null) {
                chunk = genChunks.get(IntCoords.toLong(x, y));
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
        LongMap.Keys it = genChunks.keys();
        while (it.hasNext) {
            long k = it.next();
            Chunk toRem = genChunks.get(k);
            if (!chunkCache.hasKey(IntCoords.xOfLong(k), IntCoords.yOfLong(k))) {
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
        ((ChunkLoader) this.loader).finish();
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
