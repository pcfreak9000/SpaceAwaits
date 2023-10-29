package de.pcfreak9000.spaceawaits.world;

import java.util.concurrent.Future;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongMap;

import de.pcfreak9000.spaceawaits.util.Bounds;
import de.pcfreak9000.spaceawaits.util.Direction;
import de.pcfreak9000.spaceawaits.util.IntCoords;
import de.pcfreak9000.spaceawaits.util.SpecialCache2D;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk.ChunkGenStage;
import de.pcfreak9000.spaceawaits.world.gen.IChunkGenerator;

public class TestChunkProvider implements IChunkProvider {
    
    private static enum StatusEnum {
        Ready, Busy, Unloading;
    }
    
    private static class Status {
        
        StatusEnum status;
        Future<Object> future;
        
    }
    
    private static class ChunkInfo {
        public static final int DONT_READD = 0, READD_PASSIVE = 1, READD_ACTIVE = 2;
        
        boolean needsLoading = false;
        int needsReadd = DONT_READD;
        Chunk chunk;
        int x, y;
    }
    
    private static class Context {
        LongMap<Chunk> availableChunks = new LongMap<>();
        Array<ChunkInfo> infos = new Array<>();
    }
    
    private SpecialCache2D<Chunk> cache;
    
    private LongMap<Status> statusMap = new LongMap<>();
    private World world;
    
    private IChunkLoader loader;
    
    private IChunkGenerator chunkGen;
    
    public TestChunkProvider(World world, IChunkLoader loader, IChunkGenerator chunkgen) {
        this.world = world;
        this.loader = loader;
        this.chunkGen = chunkgen;
        
        //        this.cache = new SpecialCache2D<>(152, 145, (x, y) -> requestChunk(x, y, false, true), (chunk) -> {
        //            statusMap.remove(IntCoords.toLong(chunk.getGlobalChunkX(), chunk.getGlobalChunkY()));
        //            if (chunk.isActive()) {
        //                world.removeChunk(chunk);
        //            }
        //            loader.unloadChunk(chunk);
        //        });
    }
    
    public void requestChunk(int x, int y, boolean blocking, boolean active) {
        if (!world.getBounds().inBoundsChunk(x, y))
            return;
        long key = IntCoords.toLong(x, y);
        Status status = statusMap.get(key);
        if (status == null) {
            status = new Status();
            statusMap.put(key, status);
            status.status = StatusEnum.Busy;
            Chunk chunk = loader.loadChunk(x, y);
            
            ChunkGenStage genStage = chunk.getGenStage();
            if (genStage != ChunkGenStage.Populated) {
                Context context = aquireContext(x, y, blocking);
                //***
                loadRequired(context);
                genRequired(x, y, context);
                //***
                releaseContext(context);
                status.status = StatusEnum.Ready;
            }
            
        } else if (status.status == StatusEnum.Ready) {
            Chunk chunk = cache.getFromCache(x, y);
            if (active && !chunk.isActive()) {
                world.addChunk(chunk);
            }
        }
    }
    
    private Context aquireContext(int x, int y, boolean thisthread) {
        Context context = new Context();
        prepare(x, y, ChunkGenStage.Populated.level, context, thisthread);
        return context;
    }
    
    private void releaseContext(Context context) {
        context.availableChunks.clear();
        for (ChunkInfo info : context.infos) {
            releaseBusyChunk(info);
        }
    }
    
    private void prepare(int x, int y, int genStageLevelReq, Context context, boolean thisthread) {
        if (genStageLevelReq == 0)
            return;
        context.infos.add(aquireBusyChunk(x, y, thisthread));
        for (Direction d : Direction.MOORE_NEIGHBOURS) {
            prepare(x + d.dx, y + d.dy, genStageLevelReq - 1, context, thisthread);
        }
    }
    
    private void loadRequired(Context context) {
        for (ChunkInfo info : context.infos) {
            Chunk c = info.chunk;
            if (info.needsLoading) {
                c = this.loader.loadChunk(info.x, info.y);
            }
            context.availableChunks.put(IntCoords.toLong(info.x, info.y), c);
        }
    }
    
    private void genRequired(int x, int y, Context context) {
        genRequired(x, y, ChunkGenStage.Populated.level, context);
    }
    
    private void genRequired(int x, int y, int genStageLevelReq, Context context) {
        if (genStageLevelReq == 0)
            return;
        long key = IntCoords.toLong(x, y);
        Chunk chunk = context.availableChunks.get(key);
        if (genStageLevelReq <= chunk.getGenStage().level)
            return;
        for (Direction d : Direction.ZERO_MOORE) {
            genRequired(x + d.dx, y + d.dy, genStageLevelReq - 1, context);
        }
        advanceGenStage(chunk, genStageLevelReq, context);
    }
    
    private void advanceGenStage(Chunk chunk, int genStageLevelReq, Context context) {
        Bounds bounds = null;
        if (genStageLevelReq > ChunkGenStage.Tiled.level) {
            bounds = new Bounds((chunk.getGlobalChunkX() - 1) * Chunk.CHUNK_SIZE,
                    (chunk.getGlobalChunkY() - 1) * Chunk.CHUNK_SIZE, 3 * Chunk.CHUNK_SIZE, 3 * Chunk.CHUNK_SIZE);
            bounds = Bounds.intersect(bounds, world.getBounds());
        } else {
            bounds = chunk.getBounds();
        }
        BoundedChunkProvider localprov = new BoundedChunkProvider(bounds, context.availableChunks);
        WorldArea worldarea = new WorldArea(localprov, bounds, world);
        switch (chunk.getGenStage()) {
        case Empty:
            chunk.generate(chunkGen);
            break;
        case Tiled:
            chunk.structure(chunkGen, worldarea);
            break;
        case Structured:
            chunk.populate(chunkGen, worldarea);
            break;
        default:
            break;
        }
    }
    
    private void releaseBusyChunk(ChunkInfo info) {
        long key = IntCoords.toLong(info.x, info.y);
        Status status = statusMap.get(key);
        if (status.status != StatusEnum.Busy)
            throw new IllegalStateException();
        if (info.needsReadd >= ChunkInfo.READD_PASSIVE) {
            Chunk c = cache.unfreeze(info.x, info.y);
            if (info.needsReadd == ChunkInfo.READD_ACTIVE) {
                world.addChunk(c);
            }
            status.status = StatusEnum.Ready;
        }
    }
    
    private ChunkInfo aquireBusyChunk(int x, int y, boolean thisthread) {
        //Check bounds again?
        long key = IntCoords.toLong(x, y);
        Status status = statusMap.get(key);
        ChunkInfo info = new ChunkInfo();
        info.x = x;
        info.y = y;
        if (status == null) {
            status = new Status();
            statusMap.put(key, status);
            info.needsLoading = true;
            info.needsReadd = ChunkInfo.READD_PASSIVE;
        } else if (status.status == StatusEnum.Ready && !thisthread) {
            //If on world thread, the chunk can stay active and in the cache, otherwise it needs to be removed
            Chunk chunk = cache.freeze(x, y);
            info.needsReadd = ChunkInfo.READD_PASSIVE;
            if (chunk.isActive()) {
                world.removeChunk(chunk);
                info.needsReadd = ChunkInfo.READD_ACTIVE;
            }
            info.chunk = chunk;
        }
        status.status = StatusEnum.Busy;
        return info;
    }
    
    @Override
    public int getLoadedChunkCount() {
        return 0;
    }
    
    @Override
    public Chunk getChunk(int x, int y) {
        return cache.getOrFresh(x, y);
    }
    
    private static class BoundedChunkProvider implements IChunkProvider {
        
        private Bounds bounds;
        private LongMap<Chunk> chunks;
        
        public BoundedChunkProvider(Bounds bounds, LongMap<Chunk> chunks) {
            this.bounds = bounds;
            this.chunks = chunks;
        }
        
        @Override
        public int getLoadedChunkCount() {
            return 0;
        }
        
        @Override
        public Chunk getChunk(int x, int y) {
            if (!bounds.inBounds(x * Chunk.CHUNK_SIZE, y * Chunk.CHUNK_SIZE))
                return null;
            return chunks.get(IntCoords.toLong(x, y));
        }
        
    }
    
}
