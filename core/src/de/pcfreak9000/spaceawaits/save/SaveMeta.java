package de.pcfreak9000.spaceawaits.save;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;

public class SaveMeta implements INBTSerializable {
    
    private String displayName;
    private long created;
    
    private long masterSeed;
    
    private String nameOnDisk;
    
    public SaveMeta(String nameOnDisk) {
        this.nameOnDisk = nameOnDisk;
    }
    
    public SaveMeta(String displayname, String nameOnDisk, long created, long masterSeed) {
        this.displayName = displayname;
        this.nameOnDisk = nameOnDisk;
        this.created = created;
        this.masterSeed = masterSeed;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    //TODO proper file saving for this
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
    
    @Override
    public void readNBT(NBTCompound compound) {
        //No defaults because this is crucial information and can't really be defaulted
        String displayName = compound.getString("displayName");
        long created = compound.getLong("created");
        long masterSeed = compound.getLong("mseed");
        this.displayName = displayName;
        this.created = created;
        this.masterSeed = masterSeed;
    }
    
    @Override
    public void writeNBT(NBTCompound comp) {
        comp.putString("displayName", displayName);
        comp.putLong("created", created);
        comp.putLong("mseed", masterSeed);
    }
}
