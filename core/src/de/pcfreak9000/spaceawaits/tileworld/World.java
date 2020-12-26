package de.pcfreak9000.spaceawaits.tileworld;

import de.pcfreak9000.spaceawaits.tileworld.tile.TileWorld;

public class World {
    
    private final TileWorld tileWorld;
    private final Background background;
    
    public World(TileWorld tileWorld, Background background) {
        this.tileWorld = tileWorld;
        this.background = background;
    }
    
    public TileWorld getTileWorld() {
        return this.tileWorld;
    }
    
    public Background getBackground() {
        return this.background;
    }
    
}
