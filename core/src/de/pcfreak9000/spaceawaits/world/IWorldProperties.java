package de.pcfreak9000.spaceawaits.world;

import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public interface IWorldProperties {
    
    public static IWorldProperties defaultProperties() {
        return new IWorldProperties() {
            
            @Override
            public Tile getTileOnBreak(int tx, int ty, TileLayer layer) {
                return Tile.NOTHING;
            }
        };
    }
    
    Tile getTileOnBreak(int tx, int ty, TileLayer layer);
}
