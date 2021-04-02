package de.pcfreak9000.spaceawaits.save;

import de.pcfreak9000.nbt.NBTCompound;

public class SaveMeta {
    
    public static SaveMeta ofNBT(NBTCompound compound, String diskname) {
        String displayName = compound.getStringOrDefault("displayName", "Error reading name");
        long created = compound.getLongOrDefault("created", System.currentTimeMillis());
        long masterSeed = compound.getLongOrDefault("mseed", 0);//TODO random seed?
        return new SaveMeta(displayName, diskname, created, masterSeed);
    }
    
    private String displayName;
    private long created;
    
    private long masterSeed;
    
    private String nameOnDisk;
    
    public SaveMeta(String displayname, String nameOnDisk, long created, long masterSeed) {
        this.displayName = displayname;
        this.nameOnDisk = nameOnDisk;
        this.created = created;
        this.masterSeed = masterSeed;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public void setDisplayName(String display) {
        this.displayName = display;
    }
    
    public long getCreationTime() {
        return created;
    }
    
    public long getSeed() {
        return masterSeed;
    }
    
    public String getNameOnDisk() {
        return this.nameOnDisk;
    }
    
    public NBTCompound toNBTCompound() {
        NBTCompound comp = new NBTCompound();
        comp.putString("displayName", displayName);
        comp.putLong("created", created);
        comp.putLong("mseed", masterSeed);
        return comp;
    }
}
