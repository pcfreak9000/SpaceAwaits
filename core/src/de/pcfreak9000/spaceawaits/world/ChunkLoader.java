package de.pcfreak9000.spaceawaits.world;

import java.util.HashMap;
import java.util.Map;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.util.IntCoordKey;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.gen.IChunkGenerator;

public class ChunkLoader implements IChunkLoader {
    
    private Map<IntCoordKey, Chunk> loadedChunks;
    
    private World world;
    private IChunkGenerator chunkGen;
    private IWorldSave save;
    
    public ChunkLoader(World world, IChunkGenerator chunkGen) {
        this.loadedChunks = new HashMap<>();
        this.world = world;
        this.chunkGen = chunkGen;
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
                chunkGen.regenerateChunk(chunk, this.world);
            } else {
                chunk.generate(chunkGen);
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
        c.readNBT(nbtc);
    }
    
    @Override
    public void saveChunk(Chunk c) {
        NBTCompound nbtc = (NBTCompound) c.writeNBT();
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
