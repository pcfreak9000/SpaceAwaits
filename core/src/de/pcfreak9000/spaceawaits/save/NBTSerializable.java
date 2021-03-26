package de.pcfreak9000.spaceawaits.save;

import de.pcfreak9000.nbt.NBTTag;

public interface NBTSerializable {
    
    void readNBT(NBTTag compound);
    
    NBTTag writeNBT();
}
