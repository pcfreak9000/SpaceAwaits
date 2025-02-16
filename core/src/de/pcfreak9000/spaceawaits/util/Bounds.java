package de.pcfreak9000.spaceawaits.util;

public class Bounds {
    
    //assumes that there actually is an intersection...
    public static Bounds intersect(Bounds b0, Bounds b1) {
        int ntx0 = Math.max(b0.tx, b1.tx);
        int nty0 = Math.max(b0.ty, b1.ty);
        int ntx1 = Math.min(b0.tx + b0.width, b1.tx + b1.width);
        int nty1 = Math.min(b0.ty + b0.height, b1.ty + b1.height);
        return new Bounds(ntx0, nty0, ntx1 - ntx0, nty1 - nty0);
    }
    
    private final int tx, ty, width, height;
    
    public Bounds(int tx, int ty, int width, int height) {
        this.tx = tx;
        this.ty = ty;
        this.width = width;
        this.height = height;
    }
    
    public boolean inBounds(int tx, int ty) {
        return tx >= this.tx && tx < this.width + this.tx && ty >= this.ty && ty < this.height + this.ty;
    }
    
    public boolean inBoundsf(float x, float y) {
        return x >= this.tx && x < this.width + this.tx && y >= this.ty && y < this.height + this.ty;
    }
    
    public int getTileX() {
        return tx;
    }
    
    public int getTileY() {
        return ty;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
}
