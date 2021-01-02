package de.pcfreak9000.spaceawaits.tileworld.light;

import com.badlogic.gdx.graphics.Color;

public interface AmbientLightProvider {
    
    public static AmbientLightProvider constant(Color c) {
        return (x, y) -> c.cpy();
    }
    
    Color getAmbientLightNew(int tx, int ty);
}
