package de.pcfreak9000.spaceawaits.world;

import java.util.concurrent.Future;

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
        //Status is manipulated by the world thread, but must be visible to other threads
        volatile StatusEnum status;
        volatile Future<?> future;
        volatile int busyness;//business?
        volatile ChunkInfo info;
        
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
        LongMap<ChunkInfo> infos = new LongMap<>();
        int xCenter, yCenter;
        boolean forceThisthread;
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
        
        this.cache = new SpecialCache2D<>(152, 145, (x, y) -> requestChunk(x, y, true, true), (chunk) -> {
            statusMap.remove(IntCoords.toLong(chunk.getGlobalChunkX(), chunk.getGlobalChunkY()));
            if (chunk.isActive()) {
                world.removeChunk(chunk);
            }
            loader.unloadChunk(chunk);
        });
    }
    
    public Chunk requestChunk(int x, int y, boolean blocking, boolean active) {
        if (!world.getBounds().inBoundsChunk(x, y))
            return null;
        long key = IntCoords.toLong(x, y);
        Status status = statusMap.get(key);
        if (status == null) {
            status = new Status();
            statusMap.put(key, status);
            status.status = StatusEnum.Busy;
            //TODO this could be done async, and then context creation is done in the flush or something if necessary
            Chunk chunk = loader.loadChunk(x, y);
            
            ChunkGenStage genStage = chunk.getGenStage();
            if (genStage != ChunkGenStage.Populated) {
                Context context = aquireContext(chunk, blocking);
                //***
                loadRequired(context);
                genRequired(context);
                //***
                releaseContext(context);
                //***
            }
            status.status = StatusEnum.Ready;
            if (active && !chunk.isActive()) {
                world.addChunk(chunk);
            }
            return chunk;
        } else if (status.status == StatusEnum.Ready) {
            Chunk chunk = cache.getFromCache(x, y);
            if (active && !chunk.isActive()) {
                world.addChunk(chunk);
            }
            return chunk;
        } else if (status.status == StatusEnum.Busy) {
            //Note on the busy context that this chunk needs to stay busy
            
            //if blocking, wait for finish, then create context, or somehow before but register required chunks to be available then
            //if non-blocking, create context, make required chunks which are busy stay busy, in other thread wait until
            // this chunk isn't busy anymore with the Future#get or something  
            //then do stuff
        } else if (status.status == StatusEnum.Unloading) {
            //Fuck
            //remove map from ChunkLoader, then just dont forget about the chunk which was just saved...??
        }
        return null;
    }
    
    private Context aquireContext(Chunk chunk, boolean thisthread) {
        Context context = new Context();
        context.forceThisthread = thisthread;
        //TODO identify if nonblocking but required chunks need to stay active or something
        int x = chunk.getGlobalChunkX();
        int y = chunk.getGlobalChunkY();
        context.xCenter = x;
        context.yCenter = y;
        prepare(x, y, ChunkGenStage.Populated.level, context, x, y);
        context.availableChunks.put(IntCoords.toLong(x, y), chunk);
        return context;
    }
    
    private void releaseContext(Context context) {
        context.availableChunks.clear();
        for (ChunkInfo info : context.infos.values()) {
            releaseBusyChunk(info);
        }
    }
    
    private void prepare(int x, int y, int genStageLevelReq, Context context, int xig, int yig) {
        if (genStageLevelReq == 0)
            return;
        if (!world.getBounds().inBoundsChunk(x, y))
            return;
        if (x != xig || y != yig) {
            long key = IntCoords.toLong(x, y);
            if (!context.infos.containsKey(key)) {
                context.infos.put(key, aquireBusyChunk(x, y, context.forceThisthread, context));
            }
        }
        for (Direction d : Direction.MOORE_NEIGHBOURS) {
            prepare(x + d.dx, y + d.dy, genStageLevelReq - 1, context, xig, yig);
        }
    }
    
    private void loadRequired(Context context) {
        for (ChunkInfo info : context.infos.values()) {
            Chunk c = info.chunk;
            if (info.needsLoading) {
                c = this.loader.loadChunk(info.x, info.y);
            }
            context.availableChunks.put(IntCoords.toLong(info.x, info.y), c);
        }
    }
    
    private void genRequired(Context context) {
        genRequired(context.xCenter, context.yCenter, ChunkGenStage.Populated.level, context);
    }
    
    private void genRequired(int x, int y, int genStageLevelReq, Context context) {
        if (genStageLevelReq == 0)
            return;
        if (!world.getBounds().inBoundsChunk(x, y))
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
        status.busyness--;
        if (status.busyness > 0)
            return;
        status.info = null;
        if (info.needsReadd >= ChunkInfo.READD_PASSIVE) {
            Chunk c = cache.unfreeze(info.x, info.y);
            if (info.needsReadd == ChunkInfo.READD_ACTIVE) {
                world.addChunk(c);
            }
            status.status = StatusEnum.Ready;
        } else {
            loader.unloadChunk(info.chunk);
            statusMap.remove(key);
        }
    }
    
    private ChunkInfo aquireBusyChunk(int x, int y, boolean thisthread, Context context) {
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
        } else if (status.status == StatusEnum.Ready) {
            //If on world thread, the chunk can stay active and in the cache, otherwise it needs to be removed
            if (!thisthread) {
                Chunk chunk = cache.freeze(x, y);
                info.needsReadd = ChunkInfo.READD_PASSIVE;
                if (chunk.isActive()) {
                    world.removeChunk(chunk);
                    info.needsReadd = ChunkInfo.READD_ACTIVE;
                }
                info.chunk = chunk;
            } else {
                info.chunk = cache.getFromCache(x, y);
                if(info.chunk==null)System.out.println(info.chunk);
            }
        } else if (status.status == StatusEnum.Busy) {
            status.busyness++;
            info.chunk = status.info.chunk;
            return info;
        }
        status.info = info;
        status.status = StatusEnum.Busy;
        return info;
    }
    
    @Override
    public int getLoadedChunkCount() {
        return 0;//TODO loaded chunk count
    }
    
    @Override
    public Chunk getChunk(int x, int y) {
        return cache.getOrFresh(x, y);
    }
    
    public void unloadAll() {
        this.cache.clear();
    }
    
    public void saveAll() {
        for (Chunk c : cache.values()) {
            this.loader.saveChunk(c);
        }
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
            return chunks.size;
        }
        
        @Override
        public Chunk getChunk(int x, int y) {
            if (!bounds.inBounds(x * Chunk.CHUNK_SIZE, y * Chunk.CHUNK_SIZE))
                return null;
            return chunks.get(IntCoords.toLong(x, y));
        }
        
    }
    
}
