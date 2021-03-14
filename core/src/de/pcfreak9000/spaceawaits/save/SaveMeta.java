package de.pcfreak9000.spaceawaits.save;

import de.pcfreak9000.nbt.NBTCompound;

public class SaveMeta {
    
    public static SaveMeta of(NBTCompound compound, String diskname) {
        String displayName = compound.getString("displayName");
        long lastPlayed = compound.getLong("lastPlayed");
        return new SaveMeta(displayName, lastPlayed, diskname);
    }
    
    private String displayName;
    private long lastPlayed;
    
    private String nameOnDisk;
    
    public SaveMeta(String displayname, long lastPlayed, String nameOnDisk) {
        this.displayName = displayname;
        this.lastPlayed = lastPlayed;
        this.nameOnDisk = nameOnDisk;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public long getLastPlayed() {
        return lastPlayed;
    }
    
    public String getNameOnDisk() {
        return this.nameOnDisk;
    }
}
