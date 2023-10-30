package de.pcfreak9000.spaceawaits.world;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
    
    private ExecutorService saveEx = Executors.newFixedThreadPool(2);//Maybe have a central place where executors for different purposes live
    
    private LongMap<Future<?>> saving = new LongMap<>();
    
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
    
    //the synchronized blocks on "saving" are for internal synchronization
    private void readChunk(Chunk c) {
        long key = IntCoords.toLong(c.getGlobalChunkX(), c.getGlobalChunkY());
        Future<?> csaving = null;
        synchronized (saving) {
            csaving = saving.get(key);
        }
        //Fuck - reading and writing might actually occur from different threads in the future, we need to think about what this means
        //this assumes readChunk and writeChunk are called from the same thread. Otherwise saving would also have to wait for reading and this would get a lot more complicated
        //also, dont call this in the synchronized block! then the synchronized block in the task can't be entered and we end up endlessly waitng if the task hasn't finished
        ensureDone(csaving);
        NBTCompound nbtc = save.readChunk(c.getGlobalChunkX(), c.getGlobalChunkY());
        AnnotationSerializer.deserialize(c, nbtc);
    }
    
    private void ensureDone(Future<?> fut) {
        if (fut == null || fut.isDone())
            return;
        try {
            fut.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void saveChunk(Chunk c) {
        NBTCompound nbtc = AnnotationSerializer.serialize(c);
        if (nbtc != null) {
            final long key = IntCoords.toLong(c.getGlobalChunkX(), c.getGlobalChunkY());
            synchronized (saving) {
                Future<?> existing = saving.get(key);
                saving.put(key, saveEx.submit(() -> {
                    ensureDone(existing);
                    save.writeChunk(c.getGlobalChunkX(), c.getGlobalChunkY(), nbtc);
                    synchronized (saving) {
                        saving.remove(key);
                    }
                }));
            }
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
