package de.pcfreak9000.spaceawaits.save;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;
import de.pcfreak9000.spaceawaits.world.WorldBounds;

public class WorldMeta implements INBTSerializable {
    
    public static Builder builder() {
        return new Builder();
    }
    
    private String displayName;
    
    private long created;
    
    private long worldSeed;
    private String worldGeneratorUsed;
    
    private int width;
    private int height;
    
    public String getDisplayName() {
        return displayName;
    }
    
    public long getWorldSeed() {
        return worldSeed;
    }
    
    public String getWorldGeneratorUsed() {
        return worldGeneratorUsed;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public long getCreated() {
        return created;
    }
    
    @Override
    public void readNBT(NBTCompound comp) {
        //No defaults because this is crucial information and can't really be defaulted
        displayName = (comp.getString("displayName"));
        worldSeed = (comp.getLong("worldSeed"));
        worldGeneratorUsed = (comp.getString("worldGeneratorUsed"));
        width = (comp.getInt("width"));
        height = (comp.getInt("height"));
        created = (comp.getLong("created"));
    }
    
    @Override
    public void writeNBT(NBTCompound comp) {
        comp.putString("displayName", displayName);
        comp.putLong("worldSeed", worldSeed);
        comp.putString("worldGeneratorUsed", worldGeneratorUsed);
        comp.putInt("width", width);
        comp.putInt("height", height);
        comp.putLong("created", created);
    }
    
    public static final class Builder {
        
        private WorldMeta meta = null;
        
        public Builder() {
            this.meta = new WorldMeta();
        }
        
        public Builder displayName(String dn) {
            this.meta.displayName = dn;
            return this;
        }
        
        public Builder worldSeed(long seed) {
            this.meta.worldSeed = seed;
            return this;
        }
        
        public Builder worldGenerator(String genId) {
            this.meta.worldGeneratorUsed = genId;
            return this;
        }
        
        public Builder dimensions(int w, int h) {
            this.meta.width = w;
            this.meta.height = h;
            return this;
        }
        
        public Builder dimensions(WorldBounds bounds) {
            this.meta.width = bounds.getWidth();
            this.meta.height = bounds.getHeight();
            return this;
        }
        
        public Builder created(long timestamp) {
            this.meta.created = timestamp;
            return this;
        }
        
        public Builder createdNow() {
            this.meta.created = System.currentTimeMillis();
            return this;
        }
        
        public WorldMeta create() {
            WorldMeta value = this.meta;
            this.meta = null;//a builder is only usable once
            return value;
        }
    }
    
}
