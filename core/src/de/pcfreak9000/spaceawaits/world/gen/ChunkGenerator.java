package de.pcfreak9000.spaceawaits.world.gen;

import de.pcfreak9000.spaceawaits.world.tile.Chunk;
import de.pcfreak9000.spaceawaits.world2.World;

/**
 * Generates regions of a TileWorld.
 *
 * @author pcfreak9000
 *
 */
public interface ChunkGenerator {
    
    void generateChunk(Chunk chunk, World world);
    
    default void regenerateChunk(Chunk chunk, World world) {
    }
}
