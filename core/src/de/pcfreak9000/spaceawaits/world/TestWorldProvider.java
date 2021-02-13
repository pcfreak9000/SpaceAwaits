package de.pcfreak9000.spaceawaits.world;

import java.util.function.Consumer;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.gen.WorldGenerationBundle;
import de.pcfreak9000.spaceawaits.world.tile.Chunk;

public class TestWorldProvider implements WorldProvider {
    
    private WorldGenerationBundle gen;
    
    private final Global global;
    private final Chunk[][] chunks;
    
    public TestWorldProvider(WorldGenerationBundle bundle) {
        this.gen = bundle;
        this.global = new Global();
        this.gen.getGlobalGenerator().populateGlobal(global);//Ooof... possibly to this outside the constructor, #requestGlobal or something?
        this.chunks = new Chunk[gen.getMeta().getWidthChunks()][gen.getMeta().getHeightChunks()];
    }
    
    private Chunk requestRegion(int rx, int ry) {
        if (gen.getMeta().inChunkBounds(rx, ry)) {
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
    public WorldMeta getMeta() {
        return gen.getMeta();
    }
    
    @Override
    public void requestChunk(int gcx, int gcy, Consumer<Chunk> onChunkLoaded) {
        onChunkLoaded.accept(requestRegion(gcx, gcy));
    }
    
    @Override
    public void unload(Chunk c) {
        //Do nothing
    }
    
    @Override
    public Global getGlobal() {
        return global;
    }

    
}
