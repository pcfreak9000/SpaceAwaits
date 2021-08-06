package de.pcfreak9000.spaceawaits.world.tile;

import java.util.Objects;

import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class BreakTileProgress {
    
    private float progress;
    private final int tx;
    private final int ty;
    private final TileLayer layer;
    private float last;
    
    public BreakTileProgress(int tx, int ty, TileLayer layer) {
        this.tx = tx;
        this.ty = ty;
        this.layer = layer;
    }
    
    public float getLast() {
        return last;
    }
    
    public void setLast(float f) {
        this.last = f;
    }
    
    public float getProgress() {
        return progress;
    }
    
    public void incProgress(float f) {
        this.progress += f;
    }
    
    public void setProgress(float f) {
        this.progress = f;
    }
    
    public int getX() {
        return tx;
    }
    
    public int getY() {
        return ty;
    }
    
    public TileLayer getLayer() {
        return layer;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BreakTileProgress) {
            BreakTileProgress other = (BreakTileProgress) obj;
            return other.tx == tx && other.ty == ty && other.layer == layer;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(tx, ty, layer);
    }
}
