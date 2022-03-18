package de.pcfreak9000.spaceawaits.world;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.serialize.SerializableEntityList;

public interface IUnchunkProvider {
    
    SerializableEntityList get();
    
    NBTCompound worldInfo();
    
    void unload();
    
}
