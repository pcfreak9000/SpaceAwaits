package de.pcfreak9000.spaceawaits.save;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.Random;

import com.badlogic.gdx.math.RandomXS128;

import de.omnikryptec.util.Logger;
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

//	@Override
//	public String createWorld(WorldMeta meta) {
//		String combinedID = null;
//		File worldFile = null;
//		do {
//			combinedID = createNewID(meta.getDisplayName());
//			worldFile = new File(worldsDir, combinedID);
//		} while (worldFile.exists());// In theory this could take forever but in practice it won't
//		worldFile.mkdir();
//		writeWorldMetaFor(worldFile, meta);
//		return combinedID;
//	}

	private String createNewID(String wname) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException nsae) {
			throw new InternalError("MD5 not supported", nsae);
		}
		byte[] md5Bytes = md.digest(wname.getBytes());
		Random random = new RandomXS128();
		byte[] additional = new byte[8];
		random.nextBytes(additional);
		byte[] bytes = new byte[md5Bytes.length + additional.length];
		System.arraycopy(md5Bytes, 0, bytes, 0, md5Bytes.length);
		System.arraycopy(additional, 0, bytes, md5Bytes.length, additional.length);
		return Base64.getEncoder().encodeToString(bytes).replace('/', '_').replace('+', '-').replace("==", "");
	}

//	@Override
//	public boolean hasWorld(String uuid) {
//		if (uuid == null) {// Hmm
//			return false;
//		} // TODO Include uuid in world meta and check that here?!
//		File file = new File(worldsDir, uuid);
//		return file.exists() && file.isDirectory();
//	}

//	@Override
//	public IWorldSave getWorld(String uuid) throws IOException {
//		File file = new File(worldsDir, uuid);
//		if (!file.isDirectory() || !file.exists()) {
//			throw new IllegalArgumentException();
//		}
//		WorldMeta meta = getWorldMetaFor(file);
//		writeWorldMetaFor(file, meta);// Update meta or smth...
//		WorldSave save = new WorldSave(meta, file, uuid);
//		return save;
//	}

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

//	private WorldMeta getWorldMetaFor(File world) throws IOException {
//		File metafile = new File(world, "meta.dat");
//		try (CompressedNbtReader nbtreader = new CompressedNbtReader(new FileInputStream(metafile))) {
//			NBTCompound compound = nbtreader.toCompoundTag();
//			WorldMeta m = new WorldMeta();
//			m.readNBT(compound);
//			return m;
//		}
//	}
//
//	private void writeWorldMetaFor(File world, WorldMeta meta) {
//		File metaFile = new File(world, "meta.dat");
//		try {
//			TagReader.toCompressedBinaryNBTFile(metaFile, INBTSerializable.writeNBT(meta));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	@Override
	public ILevelSave getLevel(String uuid) throws IOException {
		File file = new File(worldsDir, uuid);
		File levelInfo = new File(file, "level.dat");
		String typeid = null;
		try (CompressedNbtReader nbtreader = new CompressedNbtReader(new FileInputStream(levelInfo))) {
			NBTCompound compound = nbtreader.toCompoundTag();
			typeid = compound.getString("type");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (typeid == null) {
			throw new NoSuchElementException();
		}
		LevelType leveltype = LevelType.getLevelType(typeid);
		LevelSave levelsave = new LevelSave(leveltype, file, uuid);
		return levelsave;
	}

	@Override
	public String createLevel(LevelType type) {
		String uuid = null;
		File levelFile = null;
		do {
			uuid = createNewID(type.getClass().getName());
			levelFile = new File(worldsDir, uuid);
		} while (levelFile.exists());// In theory this could take forever but in practice it won't
		levelFile.mkdir();
		File levelInfo = new File(levelFile, "level.dat");
		NBTCompound nbtc = new NBTCompound();
		nbtc.putString("uuid", uuid);
		nbtc.putString("type", type.getTypeID());
		try {
			TagReader.toCompressedBinaryNBTFile(levelInfo, nbtc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return uuid;
	}

	@Override
	public boolean hasLevel(String uuid) {
		if (uuid == null || uuid.isBlank()) {// Hmm
			return false;
		}
		File file = new File(worldsDir, uuid);
		if (!file.exists() || !file.isDirectory()) {
			return false;
		}
		File levelInfo = new File(file, "level.dat");
		if(!levelInfo.exists()) {
			return false;
		}
		try (CompressedNbtReader nbtreader = new CompressedNbtReader(new FileInputStream(levelInfo))) {
			NBTCompound compound = nbtreader.toCompoundTag();
			if (!compound.getString("uuid").equals(uuid)) {
				Logger.getLogger(Save.class).error("UUIDs not matching");
				return false;
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
