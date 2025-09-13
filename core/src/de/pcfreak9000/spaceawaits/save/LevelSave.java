package de.pcfreak9000.spaceawaits.save;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import de.pcfreak9000.nbt.CompressedNbtReader;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.TagReader;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;

public class LevelSave implements ILevelSave {

	private LevelType type;
	private File file;
	private String uuid;

	public LevelSave(LevelType type, File file, String uuid) {
		this.type = type;
		this.file = file;
		this.uuid = uuid;
	}

	@Override
	public LevelType getLevelType() {
		return this.type;
	}

	private WorldMeta readMeta() {
		File metafile = new File(file, "meta.dat");
		try (CompressedNbtReader nbtreader = new CompressedNbtReader(new FileInputStream(metafile))) {
			NBTCompound compound = nbtreader.toCompoundTag();
			WorldMeta m = new WorldMeta();
			m.readNBT(compound);
			return m;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IWorldSave getWorldSave() {
		File file = new File(this.file, "world");
		if (!file.isDirectory() || !file.exists()) {
			throw new IllegalArgumentException();
		}
		WorldMeta meta = readMeta();
		WorldSave save = new WorldSave(meta, file, uuid);
		return save;
	}

	@Override
	public void writeMeta(WorldMeta meta) {
		File metaFile = new File(file, "meta.dat");
		if(metaFile.exists()) {
			throw new IllegalArgumentException();
		}
		File file = new File(this.file, "world");
		file.mkdir();
		try {
			TagReader.toCompressedBinaryNBTFile(metaFile, INBTSerializable.writeNBT(meta));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
