package de.pcfreak9000.spaceawaits.world;

import java.util.ArrayList;
import java.util.List;
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
        Ready, Generating;
    }
    
    private static class Status {
        StatusEnum status;
        Future<Object> future;
        boolean needLoading;
        int x, y;
        Chunk chunk;
    }
    
    private SpecialCache2D<Chunk> cache;
    
    private LongMap<Status> statusMap = new LongMap<>();
    private LongMap<Chunk> genChunks = new LongMap<>();
    private World world;
    
    private IChunkLoader loader;
    
    private IChunkGenerator chunkGen;
    
    public void requestChunk(int x, int y, boolean blocking, boolean active) {
        if (!world.getBounds().inBoundsChunk(x, y))
            return;
        long key = IntCoords.toLong(x, y);
        Status status = statusMap.get(key);
        if (status == null) {
            status = new Status();
            statusMap.put(key, status);
            status.x = x;
            status.y = y;
            status.status = StatusEnum.Generating;
            Chunk chunk = loader.loadChunk(x, y);
            status.chunk = chunk;
            
            ChunkGenStage genStage = chunk.getGenStage();
            List<Status> affectedChunks = new ArrayList<>();
            if (genStage != ChunkGenStage.Populated) {
                prepare(x, y, genStage.level + 1, affectedChunks);
            }
            //prep gen
            for (Status stat : affectedChunks) {
                if (stat.needLoading) {
                    stat.chunk = loader.loadChunk(stat.x, stat.y);
                    stat.needLoading = false;
                }
            }
            //execute gen
            genRequired(x, y, ChunkGenStage.Populated.level);
            status.status = StatusEnum.Ready;
            //readd affected chunks if required
        } else if (status.status == StatusEnum.Ready) {
            Chunk chunk = cache.getFromCache(x, y);
            if (active && !chunk.isActive()) {
                world.addChunk(chunk);
            }
        }
    }
    
    private void genRequired(int x, int y, int genStageLevelReq) {
        if (genStageLevelReq == 0)
            return;
        long key = IntCoords.toLong(x, y);
        Status status = statusMap.get(key);
        if (genStageLevelReq > status.chunk.getGenStage().level) {
            genRequired(x, y, genStageLevelReq - 1);
        }
        for (Direction d : Direction.MOORE_NEIGHBOURS) {
            genRequired(x + d.dx, y + d.dy, genStageLevelReq - 1);
        }
        Bounds bounds = new Bounds((x - 1) * Chunk.CHUNK_SIZE, (y - 1) * Chunk.CHUNK_SIZE, 3 * Chunk.CHUNK_SIZE,
                3 * Chunk.CHUNK_SIZE);
        bounds = Bounds.intersect(bounds, world.getBounds());
        BoundedChunkProvider localprov = new BoundedChunkProvider(bounds);
        WorldArea worldarea = new WorldArea(localprov, bounds, world);
        switch (status.chunk.getGenStage()) {
        case Empty:
            status.chunk.generate(chunkGen);
            break;
        case Tiled:
            status.chunk.structure(chunkGen, worldarea);
            break;
        case Structured:
            status.chunk.populate(chunkGen, worldarea);
            break;
        default:
            break;
        }
    }
    
    private void prepare(int x, int y, int genStageLevelReq, List<Status> setfutures) {
        if (genStageLevelReq == 0)
            return;
        long key = IntCoords.toLong(x, y);
        Status status = statusMap.get(key);
        if (status == null) {
            status = new Status();
            statusMap.put(key, status);
            status.needLoading = true;
        }
        //If on world thread, the chunk can stay active and in the cache, otherwise it needs to be removed
        if (status.status == StatusEnum.Ready && setfutures != null) {
            status.needLoading = false;
            Chunk chunk = cache.remove(x, y);
            if (chunk.isActive()) {
                world.removeChunk(chunk);
            }
        }
        status.status = StatusEnum.Generating;
        setfutures.add(status);
        for (Direction d : Direction.MOORE_NEIGHBOURS) {
            prepare(x + d.dx, y + d.dy, genStageLevelReq - 1, setfutures);
        }
    }
    
    @Override
    public int getLoadedChunkCount() {
        return 0;
    }
    
    @Override
    public Chunk getChunk(int x, int y) {
        return cache.getOrFresh(x, y);
    }
    
    private class BoundedChunkProvider implements IChunkProvider {
        
        private Bounds bounds;
        
        public BoundedChunkProvider(Bounds bounds) {
            this.bounds = bounds;
        }
        
        @Override
        public int getLoadedChunkCount() {
            return 0;
        }
        
        @Override
        public Chunk getChunk(int x, int y) {
            if (!bounds.inBounds(x * Chunk.CHUNK_SIZE, y * Chunk.CHUNK_SIZE))
                return null;
            return genChunks.get(IntCoords.toLong(x, y));
        }
        
    }
    
}
