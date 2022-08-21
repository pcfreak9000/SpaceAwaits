package de.pcfreak9000.spaceawaits.world.chunk;

import java.util.Objects;

import com.badlogic.gdx.physics.box2d.Fixture;

import de.pcfreak9000.spaceawaits.world.tile.ITileEntity;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class TileState {
    
    private Tile type;
    
    private Fixture fixture;
    private ITileEntity iTileEntity = null;
    
    TileState() {
    }
    
    void setTile(Tile type) {
        this.type = Objects.requireNonNull(type);
    }
    
    public Tile getTile() {
        return this.type;
    }
    
    void setTileEntity(ITileEntity te) {
        this.iTileEntity = te;
    }
    
    public ITileEntity getTileEntity() {
        return iTileEntity;
    }
    
    Fixture getFixture() {
        return fixture;
    }
    
    void setFixture(Fixture fix) {
        this.fixture = fix;
    }
    
    @Override
    public String toString() {
        return "TileState [type=" + type + ", fixture=" + fixture + ", tileEntity=" + iTileEntity + "]";
    }
    
}
