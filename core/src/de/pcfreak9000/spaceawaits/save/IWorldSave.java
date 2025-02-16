package de.pcfreak9000.spaceawaits.save;

import de.pcfreak9000.spaceawaits.world.IChunkLoader;
import de.pcfreak9000.spaceawaits.world.IGlobalLoader;

/**
 * Interface between File layer and NBT layer
 */
public interface IWorldSave {
    
    WorldMeta getWorldMeta();
    
    IGlobalLoader createGlobalLoader();
    
    //boolean hasGlobal();
    
    //NBTCompound readGlobal();
    
    //void writeGlobal(NBTCompound nbtc);
    
    IChunkLoader createChunkLoader();
    
    //boolean hasChunk(int cx, int cy);
    
    //NBTCompound readChunk(int cx, int cy);
    
    //void writeChunk(int cx, int cy, NBTCompound nbtc);
    
}
