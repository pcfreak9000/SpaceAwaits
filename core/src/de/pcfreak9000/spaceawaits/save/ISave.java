package de.pcfreak9000.spaceawaits.save;

import de.pcfreak9000.nbt.NBTCompound;

public interface ISave {
    //Players -> current location, world location as UUID
    //Global stuff
    //Tileworlds -> saved with UUID
    //The universe itself
    
    SaveMeta getSaveMeta();
    
    String createWorld(String name, WorldMeta worldMeta);
    
    IWorldSave getWorld(String uuid);
    
    boolean hasPlayer();
    
    void writePlayerNBT(NBTCompound nbtc);
    
    NBTCompound readPlayerNBT();
}
