package de.pcfreak9000.spaceawaits.content.gen;

public class SpaceSurfaceParams {
    
    private long seed;
    private int width, height;
    
    public SpaceSurfaceParams(long seed, int width, int height) {
        this.seed = seed;
        this.width = width;
        this.height = height;
    }
    
    public long getSeed() {
        return seed;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
}
