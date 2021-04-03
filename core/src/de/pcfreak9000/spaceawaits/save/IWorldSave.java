package de.pcfreak9000.spaceawaits.save;

import de.pcfreak9000.nbt.NBTCompound;

public interface IWorldSave {
    
    WorldMeta getWorldMeta();
    
    boolean hasGlobal();
    
    NBTCompound readGlobal();
    
    void writeGlobal(NBTCompound nbtc);
    
    boolean hasChunk(int cx, int cy);
    
    NBTCompound readChunk(int cx, int cy);
    
    void writeChunk(int cx, int cy, NBTCompound nbtc);
    
}
