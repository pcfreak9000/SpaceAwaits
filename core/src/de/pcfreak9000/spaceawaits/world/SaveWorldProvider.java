package de.pcfreak9000.spaceawaits.world;

import java.util.function.Consumer;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.world.gen.ChunkGenerator;
import de.pcfreak9000.spaceawaits.world.tile.Chunk;

public class SaveWorldProvider implements WorldProvider {
    
    private WorldBounds bounds;
    
    private ChunkGenerator chunkGen;
    private GlobalGenerator globalGen;
    
    private IWorldSave myWorldSave;
    
    private Global global;
    private final Chunk[][] chunks;
    
    public SaveWorldProvider(ChunkGenerator chunkGen, GlobalGenerator globaGen, IWorldSave save, WorldBounds bounds) {
        this.myWorldSave = save;
        this.chunkGen = chunkGen;
        this.globalGen = globaGen;
        this.bounds = bounds;
        this.chunks = new Chunk[bounds.getWidthChunks()][bounds.getHeightChunks()];
    }
    
    private Chunk requestChunk(int cx, int cy) {
        if (bounds.inChunkBounds(cx, cy)) {
            Chunk r = this.chunks[cx][cy];
            if (r == null) {
                r = new Chunk(cx, cy, SpaceAwaits.getSpaceAwaits().getWorldManager().getWorldAccess()); //TODO get worldaccess stuff through different means
                this.chunks[cx][cy] = r;
                if (this.myWorldSave.hasChunk(cx, cy)) {
                    //this.myWorldSave.readChunk(rx, ry);
                } else {
                    this.chunkGen.generateChunk(r, SpaceAwaits.getSpaceAwaits().getWorldManager().getWorldAccess());
                }
            }
            return r;
        }
        return null;
    }
    
    @Override
    public WorldBounds getMeta() {
        return bounds;
    }
    
    @Override
    public void requestChunk(int gcx, int gcy, Consumer<Chunk> onChunkLoaded) {
        onChunkLoaded.accept(requestChunk(gcx, gcy));
    }
    
    @Override
    public void unloadChunk(Chunk c) {
        //myWorldSave.writeChunk(0, 0, null);
    }
    
    @Override
    public Global requestGlobal() {
        if (global == null) {
            createAndPopulateGlobal();
        }
        return global;
    }
    
    @Override
    public void unloadGlobal() {
        NBTCompound nbtc = this.global.writeNBT();
        if (nbtc != null) {
            this.myWorldSave.writeGlobal(nbtc);
        }
    }
    
    private void createAndPopulateGlobal() {
        this.global = new Global();
        if (myWorldSave.hasGlobal()) {
            NBTCompound nbtc = myWorldSave.readGlobal();
            this.global.readNBT(nbtc);
        } else {
            this.globalGen.populateGlobal(global);//Also, maybe supply the WorldGenerationBundle as well?
        }
    }
}
