package de.pcfreak9000.spaceawaits.world;

import java.util.HashMap;
import java.util.Map;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.util.Direction;
import de.pcfreak9000.spaceawaits.util.IntCoordKey;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk.ChunkGenStage;
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
    
    //    boolean isLoaded(int x, int y) {
    //        return chunks.get(new IntCoordKey(x, y)) != null;
    //    }
    //    
    @Override
    public Chunk loadChunk(IntCoordKey key) {
        if (!world.getBounds().inBoundsChunk(key.getX(), key.getY())) {
            return null;
        }
        if (!this.loadedChunks.containsKey(key)) {
            Chunk c = new Chunk(key.getX(), key.getY(), this.world);
            if (save.hasChunk(key.getX(), key.getY())) {
                readChunk(c);
                chunkGen.regenerateChunk(c, this.world);
            } else {
                c.generate(chunkGen);
                //chunkGen.generateChunk(c, this.world);
            }
            this.loadedChunks.put(key, c);
            //now check if neighbouring chunks are loaded but aren't populated but can be populated
            for (Direction d : Direction.MOORE_NEIGHBOURS) {
                int x = d.dx + key.getX();
                int y = d.dy + key.getY();
                checkAndPopulate(new IntCoordKey(x, y));
            }
            //            //generate all neighbouring chunks if they dont exist so the current one can be populated
            //            for (Direction d : Direction.MOORE_NEIGHBOURS) {
            //                int x = d.dx + key.getX();
            //                int y = d.dy + key.getY();
            //                if (!world.getBounds().inBoundsChunk(x, y)) {
            //                    continue;
            //                }
            //                if (!save.hasChunk(x, y)) {
            //                    Chunk neigh = new Chunk(x, y, world);
            //                    neigh.generate(chunkGen);
            //                    saveChunk(neigh);
            //                }
            //            }
            //make sure every chunk returned by this method is also populated, might lead to weird bugs otherwise
            //            if (c.getGenStage() == ChunkGenStage.Generated) {
            //                c.populate(chunkGen);
            //            }
            checkAndPopulate(key);
            return c;
        }
        return loadedChunks.get(key);
    }
    
    private void checkAndPopulate(IntCoordKey key) {
        Chunk c = this.loadedChunks.get(key);
        if (c != null && c.getGenStage() == ChunkGenStage.Generated) {
            boolean around = true;
            for (Direction d : Direction.MOORE_NEIGHBOURS) {
                int x = d.dx + key.getX();
                int y = d.dy + key.getY();
                if (!world.getBounds().inBoundsChunk(x, y)) {
                    continue;
                }
                if (!loadedChunks.containsKey(new IntCoordKey(x, y)) && !save.hasChunk(x, y)) {
                    around = false;
                }
            }
            if (around) {
                c.populate(chunkGen);
                //Logger.getLogger(getClass()).warn("This logically shouldn't happen"); //Hmm.
            }
        }
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
