package de.pcfreak9000.spaceawaits.world;

import java.util.HashMap;
import java.util.Map;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.serialize.AnnotationSerializer;
import de.pcfreak9000.spaceawaits.util.IntCoordKey;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

public class ChunkLoader implements IChunkLoader {
    
    private Map<IntCoordKey, Chunk> loadedChunks;
    
    private World world;
    private IWorldSave save;
    
    public ChunkLoader(World world) {
        this.loadedChunks = new HashMap<>();
        this.world = world;
    }
    
    void setSave(IWorldSave save) {
        this.save = save;
    }
    
    private Chunk loadChunkActual(IntCoordKey key) {
        Chunk chunk = this.loadedChunks.get(key);
        if (chunk == null) {
            chunk = new Chunk(key.getX(), key.getY(), this.world);
            if (save.hasChunk(key.getX(), key.getY())) {
                readChunk(chunk);
            }
            this.loadedChunks.put(key, chunk);
        }
        return chunk;
    }
    
    @Override
    public Chunk loadChunk(IntCoordKey key) {
        if (!world.getBounds().inBoundsChunk(key.getX(), key.getY())) {
            return null;
        }
        Chunk chunk = loadChunkActual(key);
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
            save.writeChunk(c.getGlobalChunkX(), c.getGlobalChunkY(), nbtc);
        }
    }
    
    @Override
    public void unloadChunk(Chunk c) {
        saveChunk(c);
        loadedChunks.remove(new IntCoordKey(c.getGlobalChunkX(), c.getGlobalChunkY()));
    }
    
}
