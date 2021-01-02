package de.pcfreak9000.spaceawaits.tileworld;

import de.pcfreak9000.spaceawaits.tileworld.tile.TileWorld;

public class World {
    
    private final TileWorld tileWorld;
    private final Background background;
    private AmbientLightProvider ambientLight;
    
    public World(TileWorld tileWorld, Background background, AmbientLightProvider ambient) {
        this.tileWorld = tileWorld;
        this.background = background;
        this.ambientLight = ambient;
    }
    
    public AmbientLightProvider getAmbientLight() {
        return ambientLight;
    }
    
    public TileWorld getTileWorld() {
        return this.tileWorld;
    }
    
    public Background getBackground() {
        return this.background;
    }
    
}
