package de.pcfreak9000.spaceawaits.world;

public class ChunkCoordinateKey {
    
    private final int x, y;
    private final int hash;
    
    public ChunkCoordinateKey(int x, int y) {
        this.x = x;
        this.y = y;
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        this.hash = result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChunkCoordinateKey) {
            ChunkCoordinateKey other = (ChunkCoordinateKey) obj;
            return this.x == other.x && this.y == other.y;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return hash;
    }
    
}
