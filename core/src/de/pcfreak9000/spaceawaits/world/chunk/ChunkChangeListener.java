package de.pcfreak9000.spaceawaits.world.chunk;

import de.pcfreak9000.spaceawaits.world.tile.Tile;

public interface ChunkChangeListener {
    //Hmmmmmmmmmmmmmmm....
    void onTileStateChange(Chunk chunk, TileState state, Tile newTile, Tile oldTile, int gtx, int gty);
}
