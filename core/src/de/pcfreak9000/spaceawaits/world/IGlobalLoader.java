package de.pcfreak9000.spaceawaits.world;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.serialize.SerializableEntityList;

public interface IGlobalLoader {
    
    void load();
    
    SerializableEntityList getEntities();
    
    NBTCompound getData();
    
    void unload();
    
    void save();
    
    default void finish() {
    }
    
}
