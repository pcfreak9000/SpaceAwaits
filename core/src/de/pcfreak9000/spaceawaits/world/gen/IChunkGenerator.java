package de.pcfreak9000.spaceawaits.world.gen;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.Chunk;

/**
 * Generates regions of a TileWorld.
 *
 * @author pcfreak9000
 *
 */
public interface IChunkGenerator {
    
    void generateChunk(Chunk chunk, World world);
    
    default void regenerateChunk(Chunk chunk, World world) {
    }
}
