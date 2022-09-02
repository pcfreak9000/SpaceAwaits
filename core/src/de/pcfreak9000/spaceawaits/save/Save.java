package de.pcfreak9000.spaceawaits.save;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

import de.pcfreak9000.nbt.CompressedNbtReader;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.TagReader;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;

public class Save implements ISave {
    
    private SaveMeta meta;
    private File myDir;
    private File worldsDir;
    
    private File playerFile;
    
    public Save(SaveMeta meta, File dir) {
        this.meta = meta;
        this.myDir = dir;
        this.worldsDir = new File(myDir, "worlds");
        this.worldsDir.mkdir();
        this.playerFile = new File(myDir, "player.dat");
    }
    
    @Override
    public SaveMeta getSaveMeta() {
        return meta;
    }
    
    @Override
    public String createWorld(String name, WorldMeta meta) {
        String combinedID = null;
        File worldFile = null;
        do {
            combinedID = createNewID(name);
            worldFile = new File(worldsDir, combinedID);
        } while (worldFile.exists());//In theory this could take forever but in practice it won't
        worldFile.mkdir();
        writeWorldMetaFor(worldFile, meta);
        return combinedID;
    }
    
    private String createNewID(String wname) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsae) {
            throw new InternalError("MD5 not supported", nsae);
        }
        byte[] md5Bytes = md.digest(wname.getBytes());
        Random random = new Random();
        byte[] additional = new byte[6];
        random.nextBytes(additional);
        byte[] bytes = new byte[md5Bytes.length + additional.length];
        System.arraycopy(md5Bytes, 0, bytes, 0, md5Bytes.length);
        System.arraycopy(additional, 0, bytes, md5Bytes.length, additional.length);
        return Base64.getEncoder().encodeToString(bytes).replace('/', '_').replace('+', '-').replace("==", "");
    }
    
    @Override
    public boolean hasWorld(String uuid) {
        if (uuid == null) {//Hmm
            return false;
        }
        File file = new File(worldsDir, uuid);
        return file.exists() && file.isDirectory();
    }
    
    @Override
    public IWorldSave getWorld(String uuid) throws IOException {
        File file = new File(worldsDir, uuid);
        if (!file.isDirectory() || !file.exists()) {
            throw new IllegalArgumentException();
        }
        WorldMeta meta = getWorldMetaFor(file);
        writeWorldMetaFor(file, meta);//Update meta or smth...
        WorldSave save = new WorldSave(meta, file);
        return save;
    }
    
    @Override
    public boolean hasPlayer() {
        return this.playerFile.exists() && this.playerFile.isFile();
    }
    
    @Override
    public void writePlayerNBT(NBTCompound nbtc) {
        try {
            TagReader.toCompressedBinaryNBTFile(this.playerFile, nbtc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public NBTCompound readPlayerNBT() {
        try (CompressedNbtReader nbtreader = new CompressedNbtReader(new FileInputStream(this.playerFile))) {
            NBTCompound compound = nbtreader.toCompoundTag();
            return compound;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private WorldMeta getWorldMetaFor(File world) throws IOException {
        File metafile = new File(world, "meta.dat");
        try (CompressedNbtReader nbtreader = new CompressedNbtReader(new FileInputStream(metafile))) {
            NBTCompound compound = nbtreader.toCompoundTag();
            WorldMeta m = new WorldMeta();
            m.readNBT(compound);
            return m;
        }
    }
    
    private void writeWorldMetaFor(File world, WorldMeta meta) {
        File metaFile = new File(world, "meta.dat");
        try {
            TagReader.toCompressedBinaryNBTFile(metaFile, INBTSerializable.writeNBT(meta));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
