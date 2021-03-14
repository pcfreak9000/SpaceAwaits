package de.pcfreak9000.spaceawaits.save;

import de.pcfreak9000.nbt.NBTCompound;

public interface IWorldSave {
    
    //UUID, name, other meta stuff?
    
    WorldMeta getWorldMeta();
    
    NBTCompound readGlobal();
    
    void writeGlobal(NBTCompound nbtc);
    
    boolean hasChunk(int cx, int cy);
    
    NBTCompound readChunk(int cx, int cy);
    
    void writeChunk(int cx, int cy, NBTCompound nbtc);
    
}
