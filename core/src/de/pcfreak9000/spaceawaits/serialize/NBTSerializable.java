package de.pcfreak9000.spaceawaits.serialize;

import de.pcfreak9000.nbt.NBTTag;

@Deprecated
public interface NBTSerializable {
    
    void readNBT(NBTTag tag);
    
    NBTTag writeNBT();
}
