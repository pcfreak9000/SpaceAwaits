package de.pcfreak9000.spaceawaits.world;

public class ChunkCoordinateKey {
    
    private int x, y;
    private int hash;
    
    public void set(int x, int y) {
        if (x != this.x || y != this.y) {
            final int prime = 31;
            int result = 1;
            result = prime * result + x;
            result = prime * result + y;
            this.hash = result;
        }
        this.x = x;
        this.y = y;
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
