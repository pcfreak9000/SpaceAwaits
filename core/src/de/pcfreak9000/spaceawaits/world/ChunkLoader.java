package de.pcfreak9000.spaceawaits.world;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.utils.LongMap;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.serialize.AnnotationSerializer;
import de.pcfreak9000.spaceawaits.util.IntCoords;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

public class ChunkLoader implements IChunkLoader {
    
    private LongMap<Chunk> loadedChunks;
    
    private World world;
    private IWorldSave save;
    
    private ExecutorService saveEx = Executors.newFixedThreadPool(1);
    
    public ChunkLoader(IWorldSave save, World world) {
        this.loadedChunks = new LongMap<>();
        this.world = world;
        this.save = save;
    }
    
    private Chunk loadChunkActual(int x, int y) {
        long key = IntCoords.toLong(x, y);
        Chunk chunk = this.loadedChunks.get(key);
        if (chunk == null) {
            chunk = new Chunk(x, y, this.world);
            if (save.hasChunk(x, y)) {
                readChunk(chunk);
            }
            this.loadedChunks.put(key, chunk);
        }
        return chunk;
    }
    
    @Override
    public Chunk loadChunk(int x, int y) {
        if (!world.getBounds().inBoundsChunk(x, y)) {
            return null;
        }
        Chunk chunk = loadChunkActual(x, y);
        return chunk;
    }
    
    private void readChunk(Chunk c) {
        NBTCompound nbtc = save.readChunk(c.getGlobalChunkX(), c.getGlobalChunkY());
        AnnotationSerializer.deserialize(c, nbtc);
    }
    
    @Override
    public void saveChunk(Chunk c) {
        NBTCompound nbtc = AnnotationSerializer.serialize(c);
        if (nbtc != null) {
            saveEx.submit(() -> save.writeChunk(c.getGlobalChunkX(), c.getGlobalChunkY(), nbtc));
            //save.writeChunk(c.getGlobalChunkX(), c.getGlobalChunkY(), nbtc);       
        }
        
    }
    
    public void finish() {
        saveEx.shutdown();
        try {
            saveEx.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void unloadChunk(Chunk c) {
        saveChunk(c);
        loadedChunks.remove(IntCoords.toLong(c.getGlobalChunkX(), c.getGlobalChunkY()));
    }
    
}
