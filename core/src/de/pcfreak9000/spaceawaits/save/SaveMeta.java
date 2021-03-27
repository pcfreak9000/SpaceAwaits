package de.pcfreak9000.spaceawaits.save;

import de.pcfreak9000.nbt.NBTCompound;

public class SaveMeta {
    
    public static SaveMeta ofNBT(NBTCompound compound, String diskname) {
        String displayName = compound.getStringOrDefault("displayName", "Error reading name");
        long created = compound.getLongOrDefault("created", System.currentTimeMillis());
        return new SaveMeta(displayName, diskname, created);
    }
    
    private String displayName;
    private long created;
    
    private String nameOnDisk;
    
    public SaveMeta(String displayname, String nameOnDisk, long created) {
        this.displayName = displayname;
        this.nameOnDisk = nameOnDisk;
        this.created = created;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public long getCreationTime() {
        return created;
    }
    
    public String getNameOnDisk() {
        return this.nameOnDisk;
    }
    
    public NBTCompound toNBTCompound() {
        NBTCompound comp = new NBTCompound();
        comp.putString("displayName", displayName);
        comp.putLong("created", created);
        return comp;
    }
}
