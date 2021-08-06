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
            
            @Override
            public boolean autoLowerSpawnpointToSolidGround() {
                return true;
            }
            
            @Override
            public boolean autoWorldBorders() {
                return true;
            }
        };
    }
    
    Tile getTileOnBreak(int tx, int ty, TileLayer layer);
    
    boolean autoLowerSpawnpointToSolidGround();
    
    boolean autoWorldBorders();//Have this as property or just force world borders?
    
}
