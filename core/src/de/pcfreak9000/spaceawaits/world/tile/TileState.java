package de.pcfreak9000.spaceawaits.world.tile;

import java.util.Objects;

import com.badlogic.gdx.physics.box2d.Fixture;

public class TileState {
    
    private final int globalTileX;
    private final int globalTileY;
    
    private final Tile type;
    
    private Fixture fixture;
    private TileEntity tileEntity = null;
    
    public TileState(Tile type, int gtx, int gty) {
        this.type = Objects.requireNonNull(type);
        this.globalTileX = gtx;
        this.globalTileY = gty;
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
    
    public void setTileEntity(TileEntity te) {
        this.tileEntity = te;
    }
    
    public TileEntity getTileEntity() {
        return tileEntity;
    }
    
    Fixture getFixture() {
        return fixture;
    }
    
    void setFixture(Fixture fix) {
        this.fixture = fix;
    }
    
    @Override
    public String toString() {
        return String.format("Tile[%s, x=%d, y=%d]", this.type, this.globalTileX, this.globalTileY);
    }
}
