package de.pcfreak9000.spaceawaits.save;

import de.pcfreak9000.nbt.NBTCompound;

public interface NBTSerializable {
    
    void readNBT(NBTCompound compound);
    
    NBTCompound writeNBT();
}
