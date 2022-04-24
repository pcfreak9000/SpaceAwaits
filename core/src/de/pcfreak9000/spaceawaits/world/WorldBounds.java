package de.pcfreak9000.spaceawaits.world;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.util.Bounds;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

public class WorldBounds extends Bounds {
    
    private final int arrayWidth;
    private final int arrayHeight;
    
    public WorldBounds(int twidth, int theight) {
        super(0, 0, twidth, theight);
        this.arrayWidth = Mathf.ceili(twidth / (float) Chunk.CHUNK_SIZE);
        this.arrayHeight = Mathf.ceili(theight / (float) Chunk.CHUNK_SIZE);
    }
    
    public boolean inBoundsChunk(int cx, int cy) {
        return cx >= 0 && cx < this.arrayWidth && cy >= 0 && cy < this.arrayHeight;
    }
    
    public int getWidthChunks() {
        return arrayWidth;
    }
    
    public int getHeightChunks() {
        return arrayHeight;
    }
}
