package de.pcfreak9000.spaceawaits.world2;

import de.pcfreak9000.spaceawaits.world.ChunkCoordinateKey;

public class ChunkCoordinates {
    
    public static long toLong(int x, int y) {
        return (((long) x) << 32) | (y & 0xffffffffL);
    }
    
    public static ChunkCoordinates ofLong(long l) {
        int x = (int) (l >> 32);
        int y = (int) l;
        return new ChunkCoordinates(x, y);
    }
    
    private int x, y;
    
    public ChunkCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setChunkCoords(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getChunkX() {
        return x;
    }
    
    public int getChunkY() {
        return y;
    }
    
    public ChunkCoordinateKey createKey() {
        return new ChunkCoordinateKey(x, y);
    }
}
