package de.pcfreak9000.spaceawaits.world.gen;

import de.pcfreak9000.spaceawaits.world.WorldAccessor;
import de.pcfreak9000.spaceawaits.world.tile.Chunk;

/**
 * Generates regions of a TileWorld.
 *
 * @author pcfreak9000
 *
 */
public interface ChunkGenerator {
    
    void generateChunk(Chunk chunk, WorldAccessor worldAccess);
    
}
