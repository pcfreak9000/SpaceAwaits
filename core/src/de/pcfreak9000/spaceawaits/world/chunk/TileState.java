package de.pcfreak9000.spaceawaits.world.chunk;

import java.util.Objects;

import com.badlogic.gdx.box2d.structs.b2ShapeId;

import de.pcfreak9000.spaceawaits.world.tile.ITileEntity;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class TileState {
    
    private Tile type;
    
    private b2ShapeId fixture;
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
    
    b2ShapeId getFixture() {
        return fixture;
    }
    
    void setFixture(b2ShapeId fix) {
        this.fixture = fix;
    }
    
    @Override
    public String toString() {
        return "TileState [type=" + type + ", fixture=" + fixture + ", tileEntity=" + iTileEntity + "]";
    }
    
}
