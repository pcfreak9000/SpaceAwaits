package de.pcfreak9000.spaceawaits.world;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.world.gen.IChunkGenerator;
import de.pcfreak9000.spaceawaits.world.tile.Chunk;

public class ChunkProvider implements IChunkProvider {
    
    private Map<IntCoordKey, Chunk> chunks;
    
    private Set<IntCoordKey> queueUnload;
    
    private World world;
    private IChunkGenerator chunkGen;
    private IWorldSave save;
    
    public ChunkProvider(World world, IChunkGenerator chunkGen) {
        this.chunks = new LinkedHashMap<>();
        this.queueUnload = new LinkedHashSet<>();
        this.world = world;
        this.chunkGen = chunkGen;
    }
    
    void setSave(IWorldSave save) {
        this.save = save;
    }
    
    void dropAll() {
        chunks.clear();
        queueUnload.clear();
    }
    
    public Chunk loadChunk(int x, int y) {
        if (!world.getBounds().inChunkBounds(x, y)) {
            return null;
        }
        IntCoordKey key = new IntCoordKey(x, y);
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
        return chunks.get(new IntCoordKey(x, y));
    }
    
    @Override
    public void queueUnloadChunk(int x, int y) {
        if (world.getBounds().inChunkBounds(x, y)) {
            IntCoordKey key = new IntCoordKey(x, y);
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
        for (IntCoordKey k : chunks.keySet()) {
            queueUnload.add(k);
        }
    }
    
    @Override
    public void unloadQueued() {
        for (IntCoordKey key : queueUnload) {
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
