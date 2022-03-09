package de.pcfreak9000.spaceawaits.util;

public class IntCoords {
    
    public static long toLong(int x, int y) {
        return (((long) x) << 32) | (y & 0xffffffffL);
    }
    
    public static int xOfLong(long l) {
        return (int) (l >> 32);
    }
    
    public static int yOfLong(long l) {
        return (int) l;
    }
    
    public static IntCoords ofLong(long l) {
        int x = xOfLong(l);
        int y = yOfLong(l);
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
