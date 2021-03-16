package de.pcfreak9000.spaceawaits.world;

import java.util.function.Consumer;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.gen.WorldGenerationBundle;
import de.pcfreak9000.spaceawaits.world.tile.Chunk;

@Deprecated
public class TestWorldProvider implements WorldProvider {
    
    private WorldGenerationBundle gen;
    
    private Global global;
    private final Chunk[][] chunks;
    
    public TestWorldProvider(WorldGenerationBundle bundle) {
        this.gen = bundle;
        this.chunks = new Chunk[gen.getBounds().getWidthChunks()][gen.getBounds().getHeightChunks()];
    }
    
    private Chunk requestRegion(int rx, int ry) {
        if (gen.getBounds().inChunkBounds(rx, ry)) {
            Chunk r = this.chunks[rx][ry];
            if (r == null) {
                r = new Chunk(rx, ry, SpaceAwaits.getSpaceAwaits().getWorldManager().getWorldAccess());
                this.chunks[rx][ry] = r;
                this.gen.getChunkGenerator().generateChunk(r,
                        SpaceAwaits.getSpaceAwaits().getWorldManager().getWorldAccess());
            }
            return r;
        }
        return null;
    }
    
    @Override
    public WorldBounds getMeta() {
        return gen.getBounds();
    }
    
    @Override
    public void requestChunk(int gcx, int gcy, Consumer<Chunk> onChunkLoaded) {
        onChunkLoaded.accept(requestRegion(gcx, gcy));
    }
    
    @Override
    public void unloadChunk(Chunk c) {
        //Do nothing
    }
    
    @Override
    public Global requestGlobal() {
        if (global == null) {
            createAndPopulateGlobal();
        }
        return global;
    }
    
    private void createAndPopulateGlobal() {
        this.global = new Global();
        this.gen.getGlobalGenerator().populateGlobal(global);//Also, maybe supply the WorldGenerationBundle as well?
    }

    @Override
    public void unloadGlobal() {
        //Do nothing
    }
    
}
