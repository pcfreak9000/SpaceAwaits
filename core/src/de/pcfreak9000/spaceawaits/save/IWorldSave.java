package de.pcfreak9000.spaceawaits.save;

import de.pcfreak9000.spaceawaits.world.IChunkLoader;
import de.pcfreak9000.spaceawaits.world.IGlobalLoader;

/**
 * Interface between File layer and NBT layer
 */
public interface IWorldSave {

    WorldMeta getWorldMeta();

    IGlobalLoader createGlobalLoader();

    IChunkLoader createChunkLoader();

    String getUUID();

}
