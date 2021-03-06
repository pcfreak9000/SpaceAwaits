package de.pcfreak9000.spaceawaits.world.tile;

import java.util.Objects;

import com.badlogic.gdx.physics.box2d.Fixture;

public class TileState {
    
    private Tile type;
    
    private Fixture fixture;
    private TileEntity tileEntity = null;
    
    TileState() {
    }
    
    void setTile(Tile type) {
        this.type = Objects.requireNonNull(type);
    }
    
    public Tile getTile() {
        return this.type;
    }
    
    void setTileEntity(TileEntity te) {
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
        return "TileState [type=" + type + ", fixture=" + fixture + ", tileEntity=" + tileEntity + "]";
    }
    
}
