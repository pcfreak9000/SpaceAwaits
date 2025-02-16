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
    
    private WorldBounds bounds;
    
    public String getDisplayName() {
        return displayName;
    }
    
    public long getWorldSeed() {
        return worldSeed;
    }
    
    public String getWorldGeneratorUsed() {
        return worldGeneratorUsed;
    }
    
    public WorldBounds getBounds() {
        return bounds;
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
        int width = (comp.getInt("width"));
        int height = (comp.getInt("height"));
        bounds = new WorldBounds(width, height);
        created = (comp.getLong("created"));
    }
    
    @Override
    public void writeNBT(NBTCompound comp) {
        comp.putString("displayName", displayName);
        comp.putLong("worldSeed", worldSeed);
        comp.putString("worldGeneratorUsed", worldGeneratorUsed);
        comp.putInt("width", bounds.getWidth());
        comp.putInt("height", bounds.getHeight());
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
            this.meta.bounds = new WorldBounds(w, h);
            return this;
        }
        
        public Builder dimensions(WorldBounds bounds) {
            this.meta.bounds = bounds;
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
