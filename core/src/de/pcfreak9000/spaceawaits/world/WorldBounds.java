package de.pcfreak9000.spaceawaits.world;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

public class WorldBounds {
    
    private int tWidth;
    private int tHeight;
    private final int arrayWidth;
    private final int arrayHeight;
    
    public WorldBounds(int twidth, int theight) {
        this.tWidth = twidth;
        this.tHeight = theight;
        this.arrayWidth = Mathf.ceili(twidth / (float) Chunk.CHUNK_SIZE);
        this.arrayHeight = Mathf.ceili(theight / (float) Chunk.CHUNK_SIZE);
    }
    
    public boolean inBounds(int tx, int ty) {
        return tx >= 0 && tx < this.tWidth && ty >= 0 && ty < this.tHeight;
    }
    
    public boolean inBoundsf(float x, float y) {
        return x >= 0 && x < this.tWidth && y >= 0 && y < this.tHeight;
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
}
