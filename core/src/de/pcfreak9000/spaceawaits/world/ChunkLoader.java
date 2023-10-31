package de.pcfreak9000.spaceawaits.world;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.utils.LongMap;

import de.omnikryptec.util.Logger;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.serialize.AnnotationSerializer;
import de.pcfreak9000.spaceawaits.util.IntCoords;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

public class ChunkLoader implements IChunkLoader {
    
    private static final Logger LOGGER = Logger.getLogger(ChunkLoader.class);
    
    private static final Object MARK = new Object();
    
    //Maybe have a central place where executors for different purposes live
    private ExecutorService saveEx = Executors.newFixedThreadPool(2);
    
    private LongMap<Chunk> loadedChunks = new LongMap<>();
    private LongMap<Object> markUnload = new LongMap<>();
    private LongMap<Future<?>> saving = new LongMap<>();
    
    private World world;
    private IWorldSave save;
    
    public ChunkLoader(IWorldSave save, World world) {
        this.world = world;
        this.save = save;
    }
    
    private Chunk loadChunkActual(int x, int y) {
        long key = IntCoords.toLong(x, y);
        Chunk chunk;
        Object tester;
        synchronized (loadedChunks) {
            chunk = this.loadedChunks.get(key);
            tester = this.markUnload.remove(key);
        }
        if (chunk == null) {
            chunk = new Chunk(x, y, this.world);
            if (save.hasChunk(x, y)) {
                readChunk(chunk);
            }
            synchronized (loadedChunks) {
                this.loadedChunks.put(key, chunk);
            }
        } else {
            if (tester == null && Logger.getMinLogType() == Logger.LogType.Debug) {
                throw new RuntimeException("this chunk is already available, why load it again");
            }
            //Something was marked for removal but is required again, somewhere in the tmp chunks
            LOGGER.debug("A chunk marked for removal was required again");
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
        //Fuck - reading and writing might actually occur from different threads in the future, we need to think about what this means... nothing
        //chunks which are saved right now are not read again, they are simply returned. Only when the writing is done will they have to be read again
        //in other cases, there is a huge problem anyways with how this class is used
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
        saveChunkInternal(c, false);
    }
    
    private void saveChunkInternal(Chunk c, boolean unload) {
        NBTCompound nbtc = AnnotationSerializer.serialize(c);
        if (nbtc != null) {
            final long key = IntCoords.toLong(c.getGlobalChunkX(), c.getGlobalChunkY());
            if (unload) {
                synchronized (loadedChunks) {
                    if (!loadedChunks.containsKey(key)) {
                        //This is bad. Can occur when Thread A loads a chunk and Thread B unloads the same chunk after it was given out to A. 
                        //Should A now try to unload the chunk, this exception is raised. Should not happen in practice.
                        //If it does anyway, ... oof.
                        //Only accepting the chunk to unload from the thread it was given out to won't work, Chunks aren't threadsafe anyway
                        throw new IllegalStateException();
                    }
                    markUnload.put(key, MARK);
                }
            }
            synchronized (saving) {
                Future<?> existing = saving.get(key);
                saving.put(key, saveEx.submit(() -> {
                    ensureDone(existing);
                    save.writeChunk(c.getGlobalChunkX(), c.getGlobalChunkY(), nbtc);
                    synchronized (saving) {
                        saving.remove(key);
                    }
                    synchronized (loadedChunks) {
                        if (markUnload.containsKey(key)) {
                            loadedChunks.remove(key);
                            markUnload.remove(key);
                        }
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
        saveChunkInternal(c, true);
    }
    
}
