package de.pcfreak9000.spaceawaits.tileworld;

import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.spaceawaits.tileworld.tile.Tile;

public class WorldLoadingFence {
    
    private final Vector2 following;
    
    private int xChunkRange = 1;
    private int yChunkRange = 1;
    //Is this even useful?
    private int xChunkOffset;
    private int yChunkOffset;
    
    public WorldLoadingFence(Vector2 foll) {
        this.following = foll;
    }
    
    public int getChunkMidpointX() {
        return this.xChunkOffset + Region.toGlobalRegion(Tile.toGlobalTile(this.following.x));
    }
    
    public int getChunkMidpointY() {
        return this.yChunkOffset + Region.toGlobalRegion(Tile.toGlobalTile(this.following.y));
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
