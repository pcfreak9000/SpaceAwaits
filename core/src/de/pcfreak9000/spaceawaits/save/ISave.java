package de.pcfreak9000.spaceawaits.save;

import java.io.IOException;

import de.pcfreak9000.nbt.NBTCompound;

public interface ISave {
    // Players -> current location, world location as UUID
    // Global stuff
    // The universe itself

    SaveMeta getSaveMeta();

    boolean hasWorld(String uuid);

    String createWorld(WorldMeta worldMeta) throws IOException;

    IWorldSave getWorld(String uuid) throws IOException;

    boolean hasPlayer();

    void writePlayerNBT(NBTCompound nbtc);

    NBTCompound readPlayerNBT();
}
