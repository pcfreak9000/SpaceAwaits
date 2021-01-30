package de.pcfreak9000.spaceawaits.tileworld.tile;

import java.util.function.Consumer;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.tileworld.Background;
import de.pcfreak9000.spaceawaits.tileworld.ChunkGenerator;
import de.pcfreak9000.spaceawaits.tileworld.light.AmbientLightProvider;

public class TestWorldProvider implements WorldProvider {
    
    private final WorldMeta meta;
    
    private Background back;
    
    private AmbientLightProvider ambient;
    
    private final ChunkGenerator generator;
    
    private final Chunk[][] chunks;
    
    public TestWorldProvider(int width, int height, ChunkGenerator generator, Background b, AmbientLightProvider a) {
        this.meta = new WorldMeta(width, height, true);
        this.generator = generator;
        this.chunks = new Chunk[meta.getWidthChunks()][meta.getHeightChunks()];
        this.back = b;
        this.ambient = a;
    }
    
    private Chunk requestRegion(int rx, int ry) {
        if (meta.inChunkBounds(rx, ry)) {
            Chunk r = this.chunks[rx][ry];
            if (r == null) {
                r = new Chunk(rx, ry, SpaceAwaits.getSpaceAwaits().getWorldManager().getWorldAccess());
                this.chunks[rx][ry] = r;
                this.generator.generateChunk(r, SpaceAwaits.getSpaceAwaits().getWorldManager().getWorldAccess());
            }
            return r;
        }
        return null;
    }
    
    @Override
    public WorldMeta getMeta() {
        return meta;
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
    public Background getBackground() {
        return back;
    }

    @Override
    public AmbientLightProvider getAmbientLight() {
        return ambient;
    }
    
}
