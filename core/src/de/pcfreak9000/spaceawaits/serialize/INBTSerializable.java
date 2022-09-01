package de.pcfreak9000.spaceawaits.serialize;

import de.pcfreak9000.nbt.NBTCompound;

public interface INBTSerializable {
    void readNBT(NBTCompound nbt);
    
    void writeNBT(NBTCompound nbt);
}
