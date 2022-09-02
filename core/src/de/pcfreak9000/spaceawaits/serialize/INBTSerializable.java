package de.pcfreak9000.spaceawaits.serialize;

import de.pcfreak9000.nbt.NBTCompound;

public interface INBTSerializable {
    
    public static NBTCompound writeNBT(INBTSerializable ser) {
        NBTCompound nbtc = new NBTCompound();
        ser.writeNBT(nbtc);
        return nbtc;
    }
    
    void readNBT(NBTCompound nbt);
    
    void writeNBT(NBTCompound nbt);
}
