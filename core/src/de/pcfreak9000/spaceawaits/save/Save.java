package de.pcfreak9000.spaceawaits.save;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import de.pcfreak9000.nbt.CompressedNbtReader;
import de.pcfreak9000.nbt.CompressedNbtWriter;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.TagReader;

public class Save implements ISave {
    
    private SaveMeta meta;
    private File myDir;
    private File worldsDir;
    //  private File metaFile;
    
    public Save(SaveMeta meta, File dir) {
        this.meta = meta;
        this.myDir = dir;
        this.worldsDir = new File(myDir, "worlds");
        this.worldsDir.mkdir();
        //  this.metaFile = new File(myDir, "meta.dat");
    }
    
    @Override
    public SaveMeta getSaveMeta() {
        return meta;
    }
    
    @Override
    public String createWorld(String name, WorldMeta meta) {
        UUID fromName = UUID.nameUUIDFromBytes(name.getBytes());
        UUID random = UUID.randomUUID();
        String combinedUUID = fromName.toString() + "_" + random.toString();
        File worldFile = new File(worldsDir, combinedUUID);
        if (worldFile.exists()) {
            throw new RuntimeException(
                    "This should be more unlikely than winning in the lotto while being hit by a lightning");
        }
        worldFile.mkdir();
        writeWorldMetaFor(worldFile, meta);
        return combinedUUID;
    }
    
    @Override
    public IWorldSave getWorld(String uuid) {
        File file = new File(worldsDir, uuid);
        if (!file.isDirectory() || !file.exists()) {
            throw new IllegalArgumentException();
        }
        WorldMeta meta = getWorldMetaFor(file);
        WorldSave save = new WorldSave(meta, file);
        return save;
    }
    
    @Override
    public void writePlayerNBT(NBTCompound nbtc) {
    }
    
    @Override
    public NBTCompound readPlayerNBT() {
        return null;
    }
    
    private WorldMeta getWorldMetaFor(File world) {
        File metafile = new File(world, "meta.dat");
        try (CompressedNbtReader nbtreader = new CompressedNbtReader(new FileInputStream(metafile))) {
            NBTCompound compound = nbtreader.toCompoundTag();
            return WorldMeta.ofNBT(compound);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException();//Shouldn't happen
        } catch (IOException e) {
            throw new RuntimeException(e);//Meh...
        }
    }
    
    private void writeWorldMetaFor(File world, WorldMeta meta) {
        File metaFile = new File(world, "meta.dat");
        try (CompressedNbtWriter writer = new CompressedNbtWriter(new FileOutputStream(metaFile))) {
            TagReader.applyVisitor(writer, meta.toNBTCompound());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
