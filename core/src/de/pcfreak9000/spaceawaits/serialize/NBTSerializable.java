package de.pcfreak9000.spaceawaits.serialize;

import de.pcfreak9000.nbt.NBTTag;

public interface NBTSerializable {
    
    void readNBT(NBTTag tag);
    
    NBTTag writeNBT();
}
