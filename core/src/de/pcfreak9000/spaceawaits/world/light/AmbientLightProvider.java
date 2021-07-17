package de.pcfreak9000.spaceawaits.world.light;

import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.spaceawaits.world.tile.Tile;

public interface AmbientLightProvider {
    
    public static AmbientLightProvider constant(Color c) {
        return (x, y) -> c.cpy();
    }
    
    Color getAmbientLightNew(int tx, int ty);
    
    default float getEmptyTileTransmission(int tx, int ty) {
        return Tile.NOTHING.getLightTransmission();
    }
}
