package de.pcfreak9000.spaceawaits.tileworld;

import de.pcfreak9000.spaceawaits.tileworld.tile.Region;
import de.pcfreak9000.spaceawaits.tileworld.tile.TileWorld;

/**
 * Generates regions of a TileWorld.
 *
 * @author pcfreak9000
 *
 */
public interface RegionGenerator {
    
    void generateChunk(Region region, TileWorld tileWorld);
    
}
