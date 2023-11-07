package de.pcfreak9000.spaceawaits.world;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.OrderedSet;

import de.pcfreak9000.spaceawaits.util.Bounds;
import de.pcfreak9000.spaceawaits.util.Direction;
import de.pcfreak9000.spaceawaits.util.IntCoords;
import de.pcfreak9000.spaceawaits.util.Pipeline;
import de.pcfreak9000.spaceawaits.util.Pipeline.PipelineEntry;
import de.pcfreak9000.spaceawaits.util.SpecialCache2D;
import de.pcfreak9000.spaceawaits.util.TaskScheduler;
import de.pcfreak9000.spaceawaits.util.TaskScheduler.Task;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk.ChunkGenStage;
import de.pcfreak9000.spaceawaits.world.gen.IChunkGenerator;

public class TestChunkProvider2 implements IWorldChunkProvider {
    private static enum StatusEnum {
        Ready, Generating, Dangling, Unloading, Null, Assisting;
    }
    
    private static class Status {
        //Status is manipulated by the world thread, but must be visible to other threads
        volatile StatusEnum status = StatusEnum.Null;
        volatile int busyness;//business?
        volatile ChunkInfo info;
        volatile Task task;
        volatile Pipeline<PipeContext> currentPipeline;
    }
    
    private static class PipeContext {
        final long key;
        final int x, y;
        
        Chunk chunk;
        boolean requestActive;
        Status status;
        Context context;
        
        public PipeContext(long key, int x, int y) {
            this.key = key;
            this.x = x;
            this.y = y;
        }
        
        public void requestActive(boolean b) {
            this.requestActive = this.requestActive || b;
        }
    }
    
    private static class ChunkInfo {
        public static final int DONT_READD = 0, READD_PASSIVE = 1, READD_ACTIVE = 2;
        
        public ChunkInfo(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        // boolean needsLoading = false;
        int needsReadd = DONT_READD;
        Chunk chunk;
        final int x, y;
    }
    
    private static class Context {
        LongMap<Chunk> availableChunks = new LongMap<>();
        LongMap<ChunkInfo> infos = new LongMap<>();
        int xCenter, yCenter;
        boolean forceThisthread;
        LongArray affectedChunks = new LongArray();
    }
    
    private SpecialCache2D<Chunk> cacheReady, cacheDangling;
    private LongMap<Status> statusMap = new LongMap<>();
    private World world;
    
    private IChunkLoader loader;
    
    private IChunkGenerator chunkGen;
    
    private TaskScheduler scheduler;
    private ExecutorService ex;
    
    private ConcurrentLinkedQueue<Runnable> runOnMainThread = new ConcurrentLinkedQueue<>();
    
    private OrderedSet<Pipeline<?>> pipelines = new OrderedSet<>();
    
    //public Pipelin
    
    public TestChunkProvider2(World world, IChunkLoader loader, IChunkGenerator chunkgen) {
        this.scheduler = new TaskScheduler(ex = Executors.newFixedThreadPool(4));
        this.world = world;
        this.loader = loader;
        this.chunkGen = chunkgen;
        this.cacheReady = new SpecialCache2D<>(90, 85, (x, y) -> null, (chunk) -> {
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
        requestChunk(x, y, false, true);
    }
    
    private Status getOrCreateStatus(long key) {
        Status status = statusMap.get(key);
        if (status == null) {
            status = new Status();
            statusMap.put(key, status);
        }
        return status;
    }
    
    private class PELoad implements PipelineEntry<PipeContext> {
        
        @Override
        public boolean mainthread() {
            return false;
        }
        
        @Override
        public void run(Pipeline<PipeContext> pipeline, PipeContext context) {
            context.chunk = loader.loadChunk(context.x, context.y);
        }
    }
    
    private class PECheckGenerationStatus implements PipelineEntry<PipeContext> {
        
        @Override
        public boolean mainthread() {
            return true;
        }
        
        @Override
        public void run(Pipeline<PipeContext> pipeline, PipeContext pcontext) {
            ChunkGenStage genStage = pcontext.chunk.getGenStage();
            if (genStage != ChunkGenStage.Populated) {
                pcontext.status.status = StatusEnum.Generating;
                Context context = aquireContext(pcontext.chunk, pipeline.isForceNow());
                pcontext.context = context;
                pipeline.submit(pegen, context.affectedChunks);
                pipeline.submit(peadd);
            } else {
                peadd.run(pipeline, pcontext);
            }
            flush();
        }
    }
    
    private class PEAdd implements PipelineEntry<PipeContext> {
        
        @Override
        public boolean mainthread() {
            return true;
        }
        
        @Override
        public void run(Pipeline<PipeContext> pipeline, PipeContext context) {
            if (context.context != null) {
                releaseContext(context.context);
                context.context = null;
            }
            context.status.status = StatusEnum.Ready;
            pipelines.remove(context.status.currentPipeline);
            context.status.currentPipeline = null;
            if (context.requestActive && !context.chunk.isActive()) {
                world.addChunk(context.chunk);
            }
            cacheReady.put(context.x, context.y, context.chunk);
        }
        
    }
    
    private class PEGenerate implements PipelineEntry<PipeContext> {
        
        @Override
        public boolean mainthread() {
            return false;
        }
        
        @Override
        public void run(Pipeline<PipeContext> pipeline, PipeContext context) {
            loadRequired(context.context);
            genRequired(context.context);
        }
        
    }
    
    private final PELoad peload = new PELoad();
    private final PECheckGenerationStatus pecheckgenstat = new PECheckGenerationStatus();
    private final PEGenerate pegen = new PEGenerate();
    private final PEAdd peadd = new PEAdd();
    private final PEAddToContext peatcontext = new PEAddToContext();
    
    private void requestChunk(int x, int y, boolean blockingg, boolean active) {
        if (!world.getBounds().inBoundsChunk(x, y))
            return;
        flush();
        boolean blocking = true;
        long key = IntCoords.toLong(x, y);
        Status status = getOrCreateStatus(key);
        if (status.status == StatusEnum.Null) {
            status.status = StatusEnum.Generating;
            if (status.currentPipeline == null) {
                status.currentPipeline = new Pipeline<>(new PipeContext(key, x, y), key, scheduler);
                status.currentPipeline.getContext().status = status;
                pipelines.add(status.currentPipeline);
            }
            status.currentPipeline.getContext().requestActive(active);
            status.currentPipeline.submit(peload);
            status.currentPipeline.submit(pecheckgenstat);
            //            scheduler.submit(key, blocking, () -> {
            //                Chunk chunk = loader.loadChunk(x, y);
            //                runOnMainThread.add(() -> {
            //                    //check generation, add chunk to system
            //                    checkGeneration(status, chunk, blocking, active);
            //                });
            //            });
        } else if (status.status == StatusEnum.Dangling) {
            Chunk chunk = cacheDangling.take(x, y);
            if (status.currentPipeline == null) {
                status.currentPipeline = new Pipeline<>(new PipeContext(key, x, y), key, scheduler);
                status.currentPipeline.getContext().status = status;
                pipelines.add(status.currentPipeline);
            }
            status.currentPipeline.getContext().requestActive(active);
            status.currentPipeline.getContext().chunk = chunk;
            status.currentPipeline.submit(pecheckgenstat);
            //check generation, add chunk to system
            //checkGeneration(status, chunk, blocking, active);
        } else if (status.status == StatusEnum.Ready) {
            Chunk chunk = cacheReady.getFromCache(x, y);
            if (active && !chunk.isActive()) {
                world.addChunk(chunk);
            }
        } else if (status.status == StatusEnum.Unloading) {
            
        } else if (status.status == StatusEnum.Generating) {
            if (status.currentPipeline == null) {
                throw new IllegalStateException();
                //status.currentPipeline = new Pipeline<>(new PipeContext(key, x, y), key, scheduler);
            }
            status.currentPipeline.getContext().requestActive(active);
            //            if (blocking) {
            //                forceBlocking.put(key, OBJ);
            //                status.task.awaitFinished();
            //                flush();
            //                Chunk chunk = cacheReady.getFromCache(x, y);
            //                if (active && !chunk.isActive()) {
            //                    world.addChunk(chunk);
            //                }
            //            } else {
            //                scheduler.submit(key, true, () -> {
            //                    runOnMainThread.add(() -> {
            //                        Chunk chunk = cacheReady.getFromCache(x, y);
            //                        if (active && !chunk.isActive()) {
            //                            world.addChunk(chunk);
            //                        }
            //                    });
            //                });
            //            }
        } else if (status.status == StatusEnum.Assisting) {
            if (status.currentPipeline == null) {
                throw new IllegalStateException();
            }
            status.currentPipeline.getContext().requestActive(active);
            status.currentPipeline.submit(pecheckgenstat);
            //            scheduler.submit(key, blocking, () -> {
            //                runOnMainThread.add(() -> {
            //                    checkGeneration(status, status.info.chunk, blocking, active);
            //                });
            //            });
        }
        if (status.currentPipeline != null) {
            if (!status.currentPipeline.isRunning()) {
                status.currentPipeline.run();
            }
            if (blocking) {
                status.currentPipeline.forceNow();
            }
        }
        flush();//if blocking make sure runOnMainThread is run asap
    }
    
    @Override
    public void flush() {
        Array<Pipeline<?>> pips = pipelines.orderedItems();
        for (int i = 0; i < pips.size; i++) {
            pips.get(i).flush();
        }
        while (!runOnMainThread.isEmpty()) {
            runOnMainThread.poll().run();
        }
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
        context.affectedChunks.add(IntCoords.toLong(x, y));
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
                context.affectedChunks.add(key);
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
            //            if (info.needsLoading) {
            //                c = this.loader.loadChunk(info.x, info.y);
            //                info.chunk = c;
            //            }
            if (c == null)
                continue;
            context.availableChunks.put(IntCoords.toLong(info.x, info.y), c);
        }
    }
    
    private static class PEAddToContext implements PipelineEntry<PipeContext> {
        
        @Override
        public boolean mainthread() {
            return false;
        }
        
        @Override
        public void run(Pipeline<PipeContext> pipeline, PipeContext context) {
            synchronized (context.context.availableChunks) {
                context.context.infos.get(context.key).chunk = context.chunk;
                context.context.availableChunks.put(context.key, context.chunk);
            }
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
        if (status.status != StatusEnum.Assisting && status.status != StatusEnum.Generating)
            throw new IllegalStateException();
        status.busyness--;
        if (status.busyness > 0)
            return;
        status.currentPipeline = null;
        pipelines.remove(status.currentPipeline);
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
            if (status.currentPipeline == null) {
                status.currentPipeline = new Pipeline<>(new PipeContext(key, x, y), key, scheduler);
                status.currentPipeline.getContext().status = status;
                pipelines.add(status.currentPipeline);
            }
            status.currentPipeline.submit(peload);
            status.currentPipeline.submit(peatcontext);
            if (!status.currentPipeline.isRunning()) {
                status.currentPipeline.run();
            }
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
        } else if (status.status == StatusEnum.Assisting) {
            status.busyness++;
            info.chunk = status.info.chunk;
            return info;
        } else if (status.status == StatusEnum.Generating) {
            status.busyness++;
            status.info = info;
            return info;
        }
        status.info = info;
        status.status = StatusEnum.Assisting;
        return info;
    }
    
    @Override
    public int getLoadedChunkCount() {
        return cacheReady.size() + cacheDangling.size();
    }
    
    @Override
    public Chunk getChunk(int x, int y) {
        requestChunk(x, y, true, false);
        return cacheReady.getFromCache(x, y);
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
