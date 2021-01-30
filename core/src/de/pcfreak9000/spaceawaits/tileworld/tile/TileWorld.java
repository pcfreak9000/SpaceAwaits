package de.pcfreak9000.spaceawaits.tileworld.tile;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import de.pcfreak9000.spaceawaits.tileworld.ChunkGenerator;

public class TileWorld implements WorldProvider {
    
    private final WorldMeta meta;
    
    private final ChunkGenerator generator;
    
    private final Chunk[][] chunks;
    
    public TileWorld(int width, int height, ChunkGenerator generator) {
        this.meta = new WorldMeta(width, height, true);
        this.generator = generator;
        this.chunks = new Chunk[meta.getWidthChunks()][meta.getHeightChunks()];
    }
    
    public Chunk requestRegion(int rx, int ry) {
        if (meta.inChunkBounds(rx, ry)) {
            Chunk r = this.chunks[rx][ry];
            if (r == null) {
                r = new Chunk(rx, ry, this);
                this.chunks[rx][ry] = r;
                this.generator.generateChunk(r, this);
            }
            return r;
        }
        return null;
    }
    
    public Chunk getRegion(int rx, int ry) {
        if (meta.inChunkBounds(rx, ry)) {
            return this.chunks[rx][ry];
        }
        return null;
    }
    
    public Tile getTile(int tx, int ty) {
        int rx = Chunk.toGlobalChunk(tx);
        int ry = Chunk.toGlobalChunk(ty);
        Chunk r = requestRegion(rx, ry);
        return r == null ? null : r.getTile(tx, ty);//Meh
    }
    
    public Tile getTileBackground(int tx, int ty) {
        int rx = Chunk.toGlobalChunk(tx);
        int ry = Chunk.toGlobalChunk(ty);
        Chunk r = requestRegion(rx, ry);
        return r == null ? null : r.getBackground(tx, ty);//Meh
    }
    
    public void setTile(Tile tile, int tx, int ty) {
        int rx = Chunk.toGlobalChunk(tx);
        int ry = Chunk.toGlobalChunk(ty);
        Chunk r = requestRegion(rx, ry);
        if (r != null) {
            r.setTile(tile, tx, ty);
        }
    }
    
    public void collectTileIntersections(Collection<TileState> output, int x, int y, int w, int h,
            Predicate<TileState> predicate) {
        boolean xy = meta.inBounds(x, y);
        boolean xwyh = meta.inBounds(x + w, y + h);
        if (!xy && !xwyh) {
            return;
        }
        Set<Chunk> chunks = new HashSet<>();
        if (xy) {
            Chunk reg = requestRegion(Chunk.toGlobalChunk(x), Chunk.toGlobalChunk(y));
            chunks.add(reg);
        }
        if (xwyh) {
            Chunk reg = requestRegion(Chunk.toGlobalChunk(x + w), Chunk.toGlobalChunk(y + h));
            chunks.add(reg);
        }
        if (meta.inBounds(x + w, y)) {
            Chunk reg = requestRegion(Chunk.toGlobalChunk(x + w), Chunk.toGlobalChunk(y));
            chunks.add(reg);
        }
        if (meta.inBounds(x, y + h)) {
            Chunk reg = requestRegion(Chunk.toGlobalChunk(x), Chunk.toGlobalChunk(y + h));
            chunks.add(reg);
        }
        for (Chunk r : chunks) {
            r.tileIntersections(output, x, y, w, h, predicate);
        }
    }
    
    public int getWorldWidth() {
        return meta.getWidth();
    }
    
    public int getWorldHeight() {
        return meta.getHeight();
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
    
}
