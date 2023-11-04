package de.pcfreak9000.spaceawaits.world;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.utils.LongMap;

import de.pcfreak9000.spaceawaits.util.Bounds;
import de.pcfreak9000.spaceawaits.util.Direction;
import de.pcfreak9000.spaceawaits.util.IntCoords;
import de.pcfreak9000.spaceawaits.util.SpecialCache2D;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk.ChunkGenStage;
import de.pcfreak9000.spaceawaits.world.gen.IChunkGenerator;

public class TestChunkProvider implements IWorldChunkProvider {
    
    private static enum StatusEnum {
        Ready, Busy, Dangling, Unloading, Null;
    }
    
    private static class Status {
        //Status is manipulated by the world thread, but must be visible to other threads
        volatile StatusEnum status;
        volatile boolean primary;
        volatile int busyness;//business?
        volatile ChunkInfo info;
        
        volatile Future<?> future;
        volatile CountDownLatch latch;
    }
    
    private static class ChunkInfo {
        public static final int DONT_READD = 0, READD_PASSIVE = 1, READD_ACTIVE = 2;
        
        public ChunkInfo(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        boolean needsLoading = false;
        int needsReadd = DONT_READD;
        Chunk chunk;
        final int x, y;
        
        volatile Future<?> future;
        volatile CountDownLatch latchAwaitRequired;
        volatile CountDownLatch latchCountdownFinished;
    }
    
    private static class Context {
        LongMap<Chunk> availableChunks = new LongMap<>();
        LongMap<ChunkInfo> infos = new LongMap<>();
        int xCenter, yCenter;
        boolean forceThisthread;
    }
    
    private ExecutorService ex = Executors.newFixedThreadPool(4);
    
    private SpecialCache2D<Chunk> cacheReady, cacheDangling;
    
    private LongMap<Status> statusMap = new LongMap<>();
    private World world;
    
    private IChunkLoader loader;
    
    private IChunkGenerator chunkGen;
    
    private ConcurrentLinkedQueue<Runnable> runOnMainThread = new ConcurrentLinkedQueue<>();
    
    public TestChunkProvider(World world, IChunkLoader loader, IChunkGenerator chunkgen) {
        this.world = world;
        this.loader = loader;
        this.chunkGen = chunkgen;
        this.cacheReady = new SpecialCache2D<>(90, 85, (x, y) -> requestChunk(x, y, true, false, false), (chunk) -> {
            statusMap.get(
                    IntCoords.toLong(chunk.getGlobalChunkX(), chunk.getGlobalChunkY())).status = StatusEnum.Dangling;
            if (chunk.isActive()) {
                world.removeChunk(chunk);
            }
            cacheDangling.put(chunk.getGlobalChunkX(), chunk.getGlobalChunkY(), chunk);
        });
        this.cacheDangling = new SpecialCache2D<>(100, 90, null, (chunk) -> {
            statusMap.get(
                    IntCoords.toLong(chunk.getGlobalChunkX(), chunk.getGlobalChunkY())).status = StatusEnum.Unloading;
            if (chunk.isActive()) {
                throw new IllegalStateException();
            }
            loader.unloadChunk(chunk);
            statusMap.remove(IntCoords.toLong(chunk.getGlobalChunkX(), chunk.getGlobalChunkY()));
        });
    }
    
    public void requestChunk(int x, int y, boolean active) {
        requestChunk(x, y, false, true, true);
    }
    
    private Status getOrCreateStatus(long key) {
        Status status = statusMap.get(key);
        if (status == null) {
            status = new Status();
            status.status = StatusEnum.Null;
            statusMap.put(key, status);
        }
        return status;
    }
    
    private Chunk requestChunk(int x, int y, boolean blocking, boolean active, boolean add) {
        blocking = true;
        long timerbegin00 = System.currentTimeMillis();
        if (!world.getBounds().inBoundsChunk(x, y))
            return null;
        long key = IntCoords.toLong(x, y);
        final Status status = getOrCreateStatus(key);
        Chunk chunk = null;
        boolean checkgen = false;
        if (status.status == StatusEnum.Null) {
            //TODO this could be done async, and then context creation is done in the flush or something if necessary
            chunk = loader.loadChunk(x, y);
            checkgen = true;
        } else if (status.status == StatusEnum.Busy) {
            //Note on the busy context that this chunk needs to stay busy
            if (!status.primary) {
                checkgen = true;
                chunk = status.info.chunk;
            }
            if (blocking) {
                awaitSingle(status.future, status.latch);
            }
            
            //if blocking, wait for finish, then create context, or somehow before but register required chunks to be available then
            //if non-blocking, create context, make required chunks which are busy stay busy, in other thread wait until
            // this chunk isn't busy anymore with the Future#get or something  
            //then do stuff
        } else if (status.status == StatusEnum.Dangling) {
            chunk = cacheDangling.take(x, y);
            checkgen = true;
        } else if (status.status == StatusEnum.Ready) {
            chunk = cacheReady.getFromCache(x, y);
            blocking = true;
            add = false;
        } else if (status.status == StatusEnum.Unloading) {
            //Fuck
            //remove map from ChunkLoader, then just dont forget about the chunk which was just saved...??
        }
        
        if (checkgen) {
            ChunkGenStage genStage = chunk.getGenStage();
            if (genStage == ChunkGenStage.Populated) {
                blocking = true;
            } else {
                status.status = StatusEnum.Busy;
                status.primary = true;
                Context context = aquireContext(chunk, blocking);
                if (blocking) {
                    doGenTask(context);
                    releaseContext(context);
                } else {
                    CountDownLatch latch = new CountDownLatch(1);
                    Chunk useChunk = chunk;
                    Future<?> f = ex.submit(() -> {
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        doGenTask(context);
                        runOnMainThread.add(() -> {
                            releaseContext(context);
                            dealWithNewChunk(status, useChunk, active, true);
                        });
                    });
                    status.future = f;
                    latch.countDown();
                }
            }
            long timerend11 = System.currentTimeMillis();
            System.out.println(timerend11 - timerbegin00);
        }
        
        if (blocking) {
            return dealWithNewChunk(status, chunk, active, add);
        }
        return null;
    }
    
    @Override
    public void flush() {
        while (!runOnMainThread.isEmpty()) {
            runOnMainThread.poll().run();
        }
    }
    
    private void doGenTask(Context context) {
        loadRequired(context);
        awaitRequired(context);
        genRequired(context);
        notifyRequired(context);
    }
    
    private Chunk dealWithNewChunk(Status status, Chunk chunk, boolean active, boolean add) {
        status.status = StatusEnum.Ready;
        status.primary = false;
        int x = chunk.getGlobalChunkX();
        int y = chunk.getGlobalChunkY();
        if (active && !chunk.isActive()) {
            world.addChunk(chunk);
        }
        if (add && !cacheReady.hasKey(x, y)) {
            cacheReady.put(x, y, chunk);
        }
        return chunk;
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
                info.chunk = c;
            }
            context.availableChunks.put(IntCoords.toLong(info.x, info.y), c);
        }
    }
    
    private void awaitRequired(Context context) {
        for (ChunkInfo info : context.infos.values()) {
            if (info.latchAwaitRequired != null) {
                awaitSingle(info.future, info.latchAwaitRequired);
            }
        }
    }
    
    private void awaitSingle(Future<?> future, CountDownLatch latch) {
        if (future != null) {
            RunnableFuture<?> rf = (RunnableFuture<?>) future;
            rf.run();//Make sure computation starts asap
            try {
                rf.get();//Wait for computation to finish
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void notifyRequired(Context context) {
        for (ChunkInfo info : context.infos.values()) {
            info.latchCountdownFinished.countDown();
            //info.latch = null;
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
            if (cacheReady.hasKey(info.x, info.y)) {
                throw new IllegalStateException();
            }
            Chunk c = info.chunk;
            cacheReady.put(info.x, info.y, c);
            if (info.needsReadd == ChunkInfo.READD_ACTIVE) {
                world.addChunk(c);
            }
            status.status = StatusEnum.Ready;
        } else {
            if (!cacheReady.hasKey(info.x, info.y)) {
                status.status = StatusEnum.Dangling;
                cacheDangling.put(info.x, info.y, info.chunk);
                if (info.chunk.isActive()) {
                    world.removeChunk(info.chunk);
                }
            } else {
                status.status = StatusEnum.Ready;
            }
        }
    }
    
    private ChunkInfo aquireBusyChunk(int x, int y, boolean thisthread, Context context) {
        //Check bounds again?
        long key = IntCoords.toLong(x, y);
        Status status = getOrCreateStatus(key);
        ChunkInfo info = new ChunkInfo(x, y);
        if (status.status == StatusEnum.Null) {
            info.needsLoading = true;
            //info.needsReadd = ChunkInfo.READD_ACTIVE;
        } else if (status.status == StatusEnum.Dangling) {
            info.chunk = cacheDangling.take(x, y);
        } else if (status.status == StatusEnum.Ready) {
            //If on world thread, the chunk can stay active and in the cache, otherwise it needs to be removed
            if (!thisthread) {
                Chunk chunk = cacheReady.take(x, y);
                info.needsReadd = ChunkInfo.READD_PASSIVE;
                if (chunk.isActive()) {
                    world.removeChunk(chunk);
                    info.needsReadd = ChunkInfo.READD_ACTIVE;
                }
                info.chunk = chunk;
            } else {
                info.chunk = cacheReady.getFromCache(x, y);
            }
        } else if (status.status == StatusEnum.Busy) {
            status.busyness++;
            info.chunk = status.info.chunk;
            return info;
        }
        status.info = info;
        status.status = StatusEnum.Busy;
        
        info.latchAwaitRequired = status.latch;
        info.future = status.future;
        info.latchCountdownFinished = new CountDownLatch(1);
        status.latch = info.latchCountdownFinished;
        
        return info;
    }
    
    @Override
    public int getLoadedChunkCount() {
        return cacheReady.size() + cacheDangling.size();
    }
    
    @Override
    public Chunk getChunk(int x, int y) {
        return cacheReady.getOrFresh(x, y);
    }
    
    @Override
    public void unloadAll() {
        ex.shutdown();
        try {
            ex.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        flush();
        this.cacheReady.clear();
        this.cacheDangling.clear();
    }
    
    @Override
    public void saveAll() {
        for (Chunk c : cacheReady.values()) {
            this.loader.saveChunk(c);
        }
        for (Chunk c : cacheDangling.values()) {
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
        
        @Override
        public void requestChunk(int x, int y, boolean active) {
            throw new UnsupportedOperationException();
        }
        
    }
    
}
