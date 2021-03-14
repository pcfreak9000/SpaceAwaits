package de.pcfreak9000.spaceawaits.world;

import de.pcfreak9000.spaceawaits.world.tile.Chunk;

public class WorldBounds {
    
    private boolean wrapsAround;
    private int tWidth;
    private int tHeight;
    private final int arrayWidth;
    private final int arrayHeight;
    
    public WorldBounds(int twidth, int theight, boolean wraparound) {
        this.tWidth = twidth;
        this.tHeight = theight;
        this.wrapsAround = wraparound;
        this.arrayWidth = (int) Math.ceil(twidth / (double) Chunk.CHUNK_TILE_SIZE);//TODO use other ceil?
        this.arrayHeight = (int) Math.ceil(theight / (double) Chunk.CHUNK_TILE_SIZE);
    }
    
    public boolean inBounds(int tx, int ty) {
        return tx >= 0 && tx < this.tWidth && ty >= 0 && ty < this.tHeight;
    }
    
    public boolean inChunkBounds(int cx, int cy) {
        return cx >= 0 && cx < this.arrayWidth && cy >= 0 && cy < this.arrayHeight;
    }
    
    public int getWidth() {
        return tWidth;
    }
    
    public int getHeight() {
        return tHeight;
    }
    
    public int getWidthChunks() {
        return arrayWidth;
    }
    
    public int getHeightChunks() {
        return arrayHeight;
    }
    
    public boolean isWrappingAround() {
        return this.wrapsAround;
    }
}
