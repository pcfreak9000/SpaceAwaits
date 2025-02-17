package de.pcfreak9000.spaceawaits.world;

import de.pcfreak9000.spaceawaits.world.tile.ITileEntity;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public interface ITileArea {
    
    boolean inBounds(int tx, int ty);
    
    ITileEntity getTileEntity(int tx, int ty, TileLayer layer);
    
    Tile getTile(int tx, int ty, TileLayer layer);
    
    Tile setTile(int tx, int ty, TileLayer layer, Tile t);
    
    default Tile removeTile(int tx, int ty, TileLayer layer) {
        throw new UnsupportedOperationException();
    }
}
