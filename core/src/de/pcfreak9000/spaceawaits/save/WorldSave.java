package de.pcfreak9000.spaceawaits.save;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import de.pcfreak9000.nbt.CompressedNbtReader;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NbtReader;
import de.pcfreak9000.nbt.NbtWriter;
import de.pcfreak9000.nbt.TagReader;
import de.pcfreak9000.spaceawaits.save.regionfile.RegionFile;
import de.pcfreak9000.spaceawaits.save.regionfile.RegionFileCache;

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
    public boolean hasGlobal() {
        return globalFile.exists() && globalFile.isFile();
    }
    
    @Override
    public NBTCompound readGlobal() {
        try (CompressedNbtReader reader = new CompressedNbtReader(new FileInputStream(globalFile))) {
            return reader.toCompoundTag();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        RegionFile f = RegionFileCache.getRegionFile(myDir, cx, cy);
        return f.hasChunk(cx, cy);
    }
    
    @Override
    public NBTCompound readChunk(int cx, int cy) {
        DataInputStream is = RegionFileCache.getChunkDataInputStream(myDir, cx, cy);
        try (NbtReader reader = new NbtReader(is)) {
            return reader.toCompoundTag();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void writeChunk(int cx, int cy, NBTCompound nbtc) {
        DataOutputStream os = RegionFileCache.getChunkDataOutputStream(globalFile, cx, cy);
        try (NbtWriter writer = new NbtWriter(os)) {
            nbtc.accept(writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
