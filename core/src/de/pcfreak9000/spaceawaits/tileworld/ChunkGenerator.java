package de.pcfreak9000.spaceawaits.tileworld;

import de.pcfreak9000.spaceawaits.tileworld.tile.Chunk;
import de.pcfreak9000.spaceawaits.tileworld.tile.TestWorldProvider;

/**
 * Generates regions of a TileWorld.
 *
 * @author pcfreak9000
 *
 */
public interface ChunkGenerator {
    
    void generateChunk(Chunk chunk, TestWorldProvider testWorldProvider);
    
}
