package de.pcfreak9000.spaceawaits.world.gen;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

/**
 * Generates regions of a TileWorld.
 *
 * @author pcfreak9000
 *
 */
public interface IChunkGenerator {
    
    void generateChunk(Chunk chunk);
    
    void structureChunk(Chunk chunk, TileSystem tiles);
    
    void populateChunk(Chunk chunk, World world);
    
}
