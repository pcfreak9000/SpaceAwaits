package de.pcfreak9000.spaceawaits.util;

public class Bounds {
    
    private int tx, ty, width, height;
    
    public Bounds(int tx, int ty, int width, int height) {
        this.tx = tx;
        this.ty = ty;
        this.width = width;
        this.height = height;
    }
    
    public boolean inBounds(int tx, int ty) {
        return tx >= this.tx && tx < this.width + this.tx && ty >= this.ty && ty < this.height + this.ty;
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
