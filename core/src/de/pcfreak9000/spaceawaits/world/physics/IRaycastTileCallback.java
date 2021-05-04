package de.pcfreak9000.spaceawaits.world.physics;

import de.pcfreak9000.spaceawaits.world.tile.Tile;

public interface IRaycastTileCallback {
    
    boolean reportRayTile(Tile tile, int tx, int ty);
    
}
