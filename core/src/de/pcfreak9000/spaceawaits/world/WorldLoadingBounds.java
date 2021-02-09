package de.pcfreak9000.spaceawaits.world;

import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.spaceawaits.world.tile.Chunk;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class WorldLoadingBounds {
    
    private final Vector2 following;
    
    private int xChunkRange = 1;
    private int yChunkRange = 1;
    
    public WorldLoadingBounds(Vector2 foll) {
        this.following = foll;
    }
    
    public int getChunkMidpointX() {
        return Chunk.toGlobalChunk(Tile.toGlobalTile(this.following.x));
    }
    
    public int getChunkMidpointY() {
        return Chunk.toGlobalChunk(Tile.toGlobalTile(this.following.y));
    }
    
    public int getChunkRadiusRangeX() {
        return this.xChunkRange;
    }
    
    public int getChunkRadiusRangeY() {
        return this.yChunkRange;
    }
    
    public void setRange(int xR, int yR) {
        this.xChunkRange = xR;
        this.yChunkRange = yR;
    }
}
