package de.pcfreak9000.spaceawaits.save;

import java.io.IOException;

import de.pcfreak9000.nbt.NBTCompound;

public interface ISave {
	// Players -> current location, world location as UUID
	// Global stuff
	// The universe itself

	SaveMeta getSaveMeta();


	ILevelSave getLevel(String uuid) throws IOException;

	String createLevel(LevelType type);
	
	boolean hasLevel(String uuid);

	boolean hasPlayer();

	void writePlayerNBT(NBTCompound nbtc);

	NBTCompound readPlayerNBT();


}
