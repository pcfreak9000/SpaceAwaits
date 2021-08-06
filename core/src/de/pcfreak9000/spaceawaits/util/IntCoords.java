package de.pcfreak9000.spaceawaits.util;

public class IntCoords {
    
    public static long toLong(int x, int y) {
        return (((long) x) << 32) | (y & 0xffffffffL);
    }
    
    public static IntCoords ofLong(long l) {
        int x = (int) (l >> 32);
        int y = (int) l;
        return new IntCoords(x, y);
    }
    
    private int x, y;
    
    public IntCoords(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setCoords(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public IntCoordKey createKey() {
        return new IntCoordKey(x, y);
    }
}
