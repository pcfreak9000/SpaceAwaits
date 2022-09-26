package de.pcfreak9000.spaceawaits.save;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.pcfreak9000.nbt.CompressedNbtReader;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.TagReader;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;

public class SaveManager implements ISaveManager {
    
    private File savesDir;
    
    public SaveManager(File savesDir) {
        this.savesDir = savesDir;
        if (!savesDir.isDirectory()) {
            throw new IllegalStateException();
        }
    }
    
    //TODO check incoming strings
    
    @Override
    public boolean exists(String foldername) {
        File file = new File(savesDir, foldername);
        return file.exists();
    }
    
    @Override
    public ISave createSave(String name, long seed) {
        String display = name;
        File file = new File(savesDir, name);
        while (file.exists()) {
            name = name + "_";
            file = new File(savesDir, name);
        }
        file.mkdir();
        SaveMeta meta = new SaveMeta(display, file.getName(), System.currentTimeMillis(), seed);
        writeSaveMetaFor(file, meta);
        return new Save(meta, file);
    }
    
    @Override
    public void deleteSave(String foldername) {
        //Some checking needs to be done on those foldernames so they dont contain .. etc?
        File saveFolder = new File(savesDir, foldername);
        if (saveFolder.isDirectory() && saveFolder.exists()) {
            try {
                SaveUtils.deleteDirectoryRecursion(saveFolder.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void rename(String foldername, String newDisplayName) throws IOException {
        File saveFolder = new File(savesDir, foldername);
        if (!saveFolder.isDirectory() || !saveFolder.exists()) {
            throw new IOException("Specified save isn't a directory or doesn't exist");
        }
        SaveMeta sm = getSaveMetaFor(saveFolder);
        sm.setDisplayName(newDisplayName);
        writeSaveMetaFor(saveFolder, sm);
    }
    
    @Override
    public ISave getSave(String foldername) throws IOException {
        File saveFolder = new File(savesDir, foldername);
        if (!saveFolder.isDirectory() || !saveFolder.exists()) {
            throw new IOException("Specified save isn't a directory or doesn't exist");
        }
        SaveMeta meta = getSaveMetaFor(saveFolder);
        writeSaveMetaFor(saveFolder, meta);//Update meta if new stuff was added (time etc) -> TODO avoid this in the future (also see Save for world metas), maybe move this into the meta class itself?
        return new Save(meta, saveFolder);
    }
    
    @Override
    public List<SaveMeta> listSaves() {
        File[] files = savesDir.listFiles((pathname) -> {
            File metafile = new File(pathname, "meta.dat");
            return pathname.isDirectory() && metafile.exists() && metafile.isFile();
        });
        List<SaveMeta> list = new ArrayList<>();
        for (File f : files) {
            try {
                SaveMeta meta = getSaveMetaFor(f);
                list.add(meta);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
    
    private SaveMeta getSaveMetaFor(File save) throws IOException {
        File metafile = new File(save, "meta.dat");
        try (CompressedNbtReader nbtreader = new CompressedNbtReader(new FileInputStream(metafile))) {
            NBTCompound compound = nbtreader.toCompoundTag();
            SaveMeta m = new SaveMeta(save.getName());
            m.readNBT(compound);
            return m;
        }
    }
    
    private void writeSaveMetaFor(File save, SaveMeta meta) {
        File metaFile = new File(save, "meta.dat");
        try {
            TagReader.toCompressedBinaryNBTFile(metaFile, INBTSerializable.writeNBT(meta));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
