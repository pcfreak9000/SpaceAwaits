package de.pcfreak9000.spaceawaits.world.gen;

import de.pcfreak9000.spaceawaits.world.WorldArea;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

/**
 * Generates regions of a TileWorld.
 *
 * @author pcfreak9000
 *
 */
public interface IChunkGenerator {
    
    void generateChunk(Chunk chunk);
    
    void structureChunk(Chunk chunk, WorldArea worldarea);
    
    void populateChunk(Chunk chunk, WorldArea worldarea);
    
}
