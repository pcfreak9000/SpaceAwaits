package de.pcfreak9000.spaceawaits.world.chunk;

import de.pcfreak9000.spaceawaits.world.tile.IMetadata;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.TileEntity;

public interface ITileArea {
    
    boolean inBounds(int tx, int ty);
    
    TileEntity getTileEntity(int tx, int ty, TileLayer layer);
    
    Tile getTile(int tx, int ty, TileLayer layer);
    
    IMetadata getMetadata(int tx, int ty, TileLayer layer);
    
    Tile setTile(int tx, int ty, TileLayer layer, Tile t);
    
    default Tile removeTile(int tx, int ty, TileLayer layer) {
        throw new UnsupportedOperationException();
    }
}
