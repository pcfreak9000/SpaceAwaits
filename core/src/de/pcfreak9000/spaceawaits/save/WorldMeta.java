package de.pcfreak9000.spaceawaits.save;

import de.pcfreak9000.nbt.NBTCompound;

public class WorldMeta {
    
    public static WorldMeta ofNBT(NBTCompound comp) {
        WorldMeta meta = new WorldMeta();
        meta.setDisplayName(comp.getString("displayName"));
        meta.setWorldSeed(comp.getLong("worldSeed"));
        meta.setWorldGeneratorUsed(comp.getString("worldGeneratorUsed"));
        meta.setWidth(comp.getInt("width"));
        meta.setHeight(comp.getInt("height"));
        meta.setWrapsAround(comp.getByte("wrapsAround") != 0);
        meta.setCreated(comp.getLong("created"));
        return meta;
    }
    
    private String displayName;
    
    private long created;
    
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
    
    public long getCreated() {
        return created;
    }
    
    public void setCreated(long created) {
        this.created = created;
    }
    
    public NBTCompound toNBTCompound() {
        NBTCompound comp = new NBTCompound();
        comp.putString("displayName", displayName);
        comp.putLong("worldSeed", worldSeed);
        comp.putString("worldGeneratorUsed", worldGeneratorUsed);
        comp.putInt("width", width);
        comp.putInt("height", height);
        comp.putByte("wrapsAround", wrapsAround ? (byte) 1 : (byte) 0);
        comp.putLong("created", created);
        return comp;
    }
    
}
