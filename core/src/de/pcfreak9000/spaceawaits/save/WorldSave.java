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
import de.pcfreak9000.spaceawaits.save.regionfile.RegionFileCache;
import de.pcfreak9000.spaceawaits.world.ChunkLoader;
import de.pcfreak9000.spaceawaits.world.GlobalLoader;
import de.pcfreak9000.spaceawaits.world.IChunkLoader;
import de.pcfreak9000.spaceawaits.world.IGlobalLoader;

public class WorldSave implements IWorldSave {

    private WorldMeta meta;
    private File myDir;

    private File globalFile;

    private final String uuid;

    public WorldSave(WorldMeta meta, File dir, String uuid) {
        this.meta = meta;
        this.myDir = dir;
        this.uuid = uuid;
        this.globalFile = new File(myDir, "global.dat");
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public WorldMeta getWorldMeta() {
        return meta;
    }

    public boolean hasGlobal() {
        return globalFile.exists() && globalFile.isFile();
    }

    public NBTCompound readGlobal() {
        try (CompressedNbtReader reader = new CompressedNbtReader(new FileInputStream(globalFile))) {
            return reader.toCompoundTag();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeGlobal(NBTCompound nbtc) {
        try {
            TagReader.toCompressedBinaryNBTFile(globalFile, nbtc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasChunk(int cx, int cy) {
        return RegionFileCache.hasChunk(myDir, cx, cy);
    }

    public NBTCompound readChunk(int cx, int cy) {
        DataInputStream is = RegionFileCache.getChunkDataInputStream(myDir, cx, cy);
        try (NbtReader reader = new NbtReader(is)) {
            return reader.toCompoundTag();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeChunk(int cx, int cy, NBTCompound nbtc) {
        DataOutputStream os = RegionFileCache.getChunkDataOutputStream(myDir, cx, cy);
        try (NbtWriter writer = new NbtWriter(os)) {
            nbtc.accept(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IChunkLoader createChunkLoader() {
        return new ChunkLoader(this, meta.getBounds());
    }

    @Override
    public IGlobalLoader createGlobalLoader() {
        return new GlobalLoader(this);
    }

}
