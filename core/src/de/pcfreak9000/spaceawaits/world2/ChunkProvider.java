package de.pcfreak9000.spaceawaits.world2;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.world.ChunkCoordinateKey;
import de.pcfreak9000.spaceawaits.world.gen.ChunkGenerator;
import de.pcfreak9000.spaceawaits.world.tile.Chunk;

public class ChunkProvider implements IChunkProvider {
    
    private Map<ChunkCoordinateKey, Chunk> chunks;
    
    private Set<ChunkCoordinateKey> queueUnload;
    
    private World world;
    private ChunkGenerator chunkGen;
    private IWorldSave save;
    
    public ChunkProvider(World world, ChunkGenerator chunkGen, IWorldSave save) {
        this.chunks = new LinkedHashMap<>();
        this.queueUnload = new LinkedHashSet<>();
        this.world = world;
        this.chunkGen = chunkGen;
        this.save = save;
    }
    
    public Chunk loadChunk(int x, int y) {
        if (!world.getBounds().inChunkBounds(x, y)) {
            return null;
        }
        ChunkCoordinateKey key = new ChunkCoordinateKey(x, y);
        Chunk c = chunks.get(key);
        queueUnload.remove(key);
        if (c == null) {
            c = new Chunk(x, y, this.world);
            if (save.hasChunk(x, y)) {
                readChunk(c);
                chunkGen.regenerateChunk(c, this.world);
            } else {
                chunkGen.generateChunk(c, this.world);
            }
            chunks.put(key, c);
        }
        return c;
    }
    
    @Override
    public Chunk getChunk(int x, int y) {
        if (!world.getBounds().inChunkBounds(x, y)) {
            return null;
        }
        return chunks.get(new ChunkCoordinateKey(x, y));
    }
    
    @Override
    public void queueUnloadChunk(int x, int y) {
        if (world.getBounds().inChunkBounds(x, y)) {
            ChunkCoordinateKey key = new ChunkCoordinateKey(x, y);
            queueUnload.add(key);
        }
    }
    
    @Override
    public int loadedChunkCount() {
        return chunks.size();
    }
    
    public void saveAll() {
        for (Chunk c : chunks.values()) {
            saveChunk(c);
        }
    }
    
    @Override
    public void queueUnloadAll() {
        for (ChunkCoordinateKey k : chunks.keySet()) {
            queueUnload.add(k);
        }
    }
    
    @Override
    public void unloadQueued() {
        for (ChunkCoordinateKey key : queueUnload) {
            Chunk c = chunks.remove(key);
            if (c != null) {
                saveChunk(c);
            }
        }
        queueUnload.clear();
    }
    
    private void readChunk(Chunk c) {
        NBTCompound nbtc = save.readChunk(c.getGlobalChunkX(), c.getGlobalChunkY());
        c.readNBT(nbtc);
    }
    
    private void saveChunk(Chunk c) {
        NBTCompound nbtc = (NBTCompound) c.writeNBT();
        if (nbtc != null) {
            save.writeChunk(c.getGlobalChunkX(), c.getGlobalChunkY(), nbtc);
        }
    }
    
}
