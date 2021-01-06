package de.pcfreak9000.spaceawaits.tileworld.tile;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import de.pcfreak9000.spaceawaits.tileworld.RegionGenerator;

public class TileWorld {
    
    //in tiles
    private final int width;
    private final int height;
    
    private final int arrayWidth;
    private final int arrayHeight;
    
    private final RegionGenerator generator;
    
    private final Chunk[][] chunks;
    
    private boolean wrapsAround = true;//TODO wrapping around
    
    public TileWorld(int width, int height, RegionGenerator generator) {
        this.width = width;
        this.height = height;
        this.arrayWidth = (int) Math.ceil(width / (double) Chunk.CHUNK_TILE_SIZE);//TODO use other ceil?
        this.arrayHeight = (int) Math.ceil(height / (double) Chunk.CHUNK_TILE_SIZE);
        this.generator = generator;
        this.chunks = new Chunk[this.arrayWidth][this.arrayHeight];
    }
    
    public Chunk requestRegion(int rx, int ry) {
        if (inRegionBounds(rx, ry)) {
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
        if (inRegionBounds(rx, ry)) {
            return this.chunks[rx][ry];
        }
        return null;
    }
    
    public boolean inRegionBounds(int rx, int ry) {
        return rx >= 0 && rx < this.arrayWidth && ry >= 0 && ry < this.arrayHeight;
    }
    
    public boolean inBounds(int tx, int ty) {
        return tx >= 0 && tx < this.width && ty >= 0 && ty < this.height;
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
        boolean xy = inBounds(x, y);
        boolean xwyh = inBounds(x + w, y + h);
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
        if (inBounds(x + w, y)) {
            Chunk reg = requestRegion(Chunk.toGlobalChunk(x + w), Chunk.toGlobalChunk(y));
            chunks.add(reg);
        }
        if (inBounds(x, y + h)) {
            Chunk reg = requestRegion(Chunk.toGlobalChunk(x), Chunk.toGlobalChunk(y + h));
            chunks.add(reg);
        }
        for (Chunk r : chunks) {
            r.tileIntersections(output, x, y, w, h, predicate);
        }
    }
    
    public int getWorldWidth() {
        return this.width;
    }
    
    public int getWorldHeight() {
        return this.height;
    }
    
}
