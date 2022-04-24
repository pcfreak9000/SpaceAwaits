package de.pcfreak9000.spaceawaits.world.gen;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

/**
 * Generates regions of a TileWorld.
 *
 * @author pcfreak9000
 *
 */
public interface IChunkGenerator {
    
    void generateChunk(Chunk chunk);
    
    void populateChunk(Chunk chunk, World world);
    
    @Deprecated
    default void regenerateChunk(Chunk chunk, World world) {
    }
    
}
