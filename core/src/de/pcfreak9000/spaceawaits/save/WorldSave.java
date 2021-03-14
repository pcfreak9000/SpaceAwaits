package de.pcfreak9000.spaceawaits.save;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import de.pcfreak9000.nbt.CompressedNbtReader;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.TagReader;

public class WorldSave implements IWorldSave {
    
    private WorldMeta meta;
    private File myDir;
    
    private File globalFile;
    
    public WorldSave(WorldMeta meta, File dir) {
        this.meta = meta;
        this.myDir = dir;
        this.globalFile = new File(myDir, "global.dat");
    }
    
    @Override
    public WorldMeta getWorldMeta() {
        return meta;
    }
    
    @Override
    public NBTCompound readGlobal() {
        try (CompressedNbtReader reader = new CompressedNbtReader(new FileInputStream(globalFile))) {
            return reader.toCompoundTag();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public void writeGlobal(NBTCompound nbtc) {
        try {
            TagReader.toCompressedBinaryNBTFile(globalFile, nbtc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean hasChunk(int cx, int cy) {
        return false;
    }
    
    @Override
    public NBTCompound readChunk(int cx, int cy) {
        return null;
    }
    
    @Override
    public void writeChunk(int cx, int cy, NBTCompound nbtc) {
    }
    
}
