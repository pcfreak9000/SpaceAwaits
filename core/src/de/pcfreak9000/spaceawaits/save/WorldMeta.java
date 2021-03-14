package de.pcfreak9000.spaceawaits.save;

public class WorldMeta {
    
    private String displayName;
    private long lastPlayed;
    
    private long worldSeed;
    private String worldGeneratorUsed;
    
    private int width;
    private int height;
    private boolean wrapsAround;
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public long getLastPlayed() {
        return lastPlayed;
    }
    
    public void setLastPlayed(long lastPlayed) {
        this.lastPlayed = lastPlayed;
    }
    
    public long getWorldSeed() {
        return worldSeed;
    }
    
    public void setWorldSeed(long worldSeed) {
        this.worldSeed = worldSeed;
    }
    
    public String getWorldGeneratorUsed() {
        return worldGeneratorUsed;
    }
    
    public void setWorldGeneratorUsed(String worldGeneratorUsed) {
        this.worldGeneratorUsed = worldGeneratorUsed;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public boolean isWrapsAround() {
        return wrapsAround;
    }
    
    public void setWrapsAround(boolean wrapsAround) {
        this.wrapsAround = wrapsAround;
    }

    @Override
    public String toString() {
        return "WorldMeta [displayName=" + displayName + ", lastPlayed=" + lastPlayed + ", worldSeed=" + worldSeed
                + ", worldGeneratorUsed=" + worldGeneratorUsed + ", width=" + width + ", height=" + height
                + ", wrapsAround=" + wrapsAround + "]";
    }
    
}
