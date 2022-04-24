package de.pcfreak9000.spaceawaits.world;

import de.pcfreak9000.spaceawaits.util.Bounds;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.TileInterface;
import de.pcfreak9000.spaceawaits.world.tile.IMetadata;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.TileEntity;

public class TileChunkArea implements TileInterface {
    
    private Chunk chunk;
    private Bounds bounds;
    
    public TileChunkArea(Chunk chunk, int tx, int ty, int width, int height) {
        this.chunk = chunk;
        this.bounds = new Bounds(tx, ty, width, height);
    }
    
    public int getTileX() {
        return bounds.getTileX();
    }
    
    public int getTileY() {
        return bounds.getTileY();
    }
    
    public int getWidth() {
        return bounds.getWidth();
    }
    
    public int getHeight() {
        return bounds.getHeight();
    }
    
    @Override
    public boolean inBounds(int tx, int ty) {
        return chunk.inBounds(tx, ty) && this.bounds.inBounds(tx, ty);
    }
    
    @Override
    public TileEntity getTileEntity(int tx, int ty, TileLayer layer) {
        if (!check(tx, ty)) {
            return null;
        }
        return chunk.getTileEntity(tx, ty, layer);
    }
    
    @Override
    public Tile getTile(int tx, int ty, TileLayer layer) {
        if (!check(tx, ty)) {
            return null;
        }
        return chunk.getTile(tx, ty, layer);
    }
    
    @Override
    public IMetadata getMetadata(int tx, int ty, TileLayer layer) {
        if (!check(tx, ty)) {
            return null;
        }
        return chunk.getMetadata(tx, ty, layer);
    }
    
    @Override
    public Tile setTile(int tx, int ty, TileLayer layer, Tile t) {
        if (!check(tx, ty)) {
            return null;
        }
        return chunk.setTile(tx, ty, layer, t);
    }
    
    private boolean check(int tx, int ty) {
        if (!bounds.inBounds(tx, ty)) {
            throw new IllegalArgumentException(); //kinda non-ideal
        }
        if (!chunk.inBounds(tx, ty)) {
            return false;
        }
        return true;
    }
}
