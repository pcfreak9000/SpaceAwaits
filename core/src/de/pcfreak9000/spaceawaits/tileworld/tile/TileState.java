package de.pcfreak9000.spaceawaits.tileworld.tile;

import java.util.Objects;

import com.badlogic.gdx.graphics.Color;

public class TileState {
    
    private final int globalTileX;
    private final int globalTileY;
    
    private final Color light;
    private final Color sunlight;
    private boolean directSun;
    
    private final Tile type;
    
    private TileEntity tileEntity = null;
    
    public TileState(Tile type, int gtx, int gty) {
        this.type = Objects.requireNonNull(type);
        this.light = new Color(0, 0, 0, 1);
        this.sunlight = new Color(0, 0, 0, 1);
        this.globalTileX = gtx;
        this.globalTileY = gty;
    }
    
    public Color light() {
        return this.light;
    }
    
    public Color sunlight() {
        return this.sunlight;
    }
    
    public Tile getTile() {
        return this.type;
    }
    
    public int getGlobalTileX() {
        return this.globalTileX;
    }
    
    public int getGlobalTileY() {
        return this.globalTileY;
    }
    
    public void setDirectSun(boolean b) {
        this.directSun = b;
    }
    
    public boolean isDirectSun() {
        return directSun;
    }
    
    public void setTileEntity(TileEntity te) {
        this.tileEntity = te;
    }
    
    public TileEntity getTileEntity() {
        return tileEntity;
    }
    
    @Override
    public String toString() {
        return String.format("Tile[%s, x=%d, y=%d]", this.type, this.globalTileX, this.globalTileY);
    }
}
