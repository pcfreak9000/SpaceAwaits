package de.pcfreak9000.spaceawaits.save;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.pcfreak9000.nbt.CompressedNbtReader;
import de.pcfreak9000.nbt.CompressedNbtWriter;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.TagReader;
import de.pcfreak9000.spaceawaits.util.Util;

public class SaveManager implements ISaveManager {
    
    private File savesDir;
    
    public SaveManager(File savesDir) {
        this.savesDir = savesDir;
        if (!savesDir.isDirectory()) {
            throw new IllegalStateException();
        }
    }
    
    @Override
    public boolean exists(String foldername) {
        File file = new File(savesDir, foldername);
        return file.exists();
    }
    
    @Override
    public ISave createSave(String name) {
        File file = new File(savesDir, name);
        while (file.exists()) {
            name = name + "_";
            file = new File(savesDir, name);
        }
        file.mkdir();
        SaveMeta meta = new SaveMeta(name, System.currentTimeMillis(), file.getName());
        writeSaveMetaFor(file, meta);
        return new Save(meta, file);
    }
    
    @Override
    public void deleteSave(String foldername) {
        //There might needs to be some checking done on those foldernames so they dont contain .. etc?
        File saveFolder = new File(savesDir, foldername);
        if (saveFolder.isDirectory() && saveFolder.exists()) {
            try {
                Util.deleteDirectoryRecursion(saveFolder.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public ISave getSave(String foldername) {
        File saveFolder = new File(savesDir, foldername);
        if (!saveFolder.isDirectory() || !saveFolder.exists()) {
            return null;
        }
        SaveMeta meta = getSaveMetaFor(saveFolder);
        return new Save(meta, saveFolder);
    }
    
    @Override
    public List<SaveMeta> listSaves() {
        File[] files = savesDir.listFiles((pathname) -> {
            File metafile = new File(pathname, "meta.dat");
            return pathname.isDirectory() && new File(pathname, "meta.dat").exists() && metafile.isFile();
        });
        List<SaveMeta> list = new ArrayList<>();
        for (File f : files) {
            list.add(getSaveMetaFor(f));
        }
        return list;
    }
    
    private SaveMeta getSaveMetaFor(File save) {
        File metafile = new File(save, "meta.dat");
        try (CompressedNbtReader nbtreader = new CompressedNbtReader(new FileInputStream(metafile))) {
            NBTCompound compound = nbtreader.toCompoundTag();
            return SaveMeta.ofNBT(compound, save.getName());
        } catch (FileNotFoundException e) {
            throw new IllegalStateException();//Shouldn't happen
        } catch (IOException e) {
            throw new RuntimeException(e);//Meh...
        }
    }
    
    private void writeSaveMetaFor(File save, SaveMeta meta) {
        File metaFile = new File(save, "meta.dat");
        try (CompressedNbtWriter writer = new CompressedNbtWriter(new FileOutputStream(metaFile))) {
            TagReader.applyVisitor(writer, meta.toNBTCompound());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
