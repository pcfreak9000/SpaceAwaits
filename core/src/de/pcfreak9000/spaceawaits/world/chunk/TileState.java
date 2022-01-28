package de.pcfreak9000.spaceawaits.world.chunk;

import java.util.Objects;

import com.badlogic.gdx.physics.box2d.Fixture;

import de.pcfreak9000.spaceawaits.world.tile.IMetadata;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.TileEntity;

public class TileState {
    
    private Tile type;
    
    private Fixture fixture;
    private TileEntity tileEntity = null;
    private IMetadata metadata = null;
    
    TileState() {
    }
    
    void setTile(Tile type) {
        Tile oldtype = this.type;
        this.type = Objects.requireNonNull(type);
        if (oldtype == this.type && this.metadata != null) {
            this.metadata.reset();
        } else {
            this.metadata = null;
            if (this.type.hasMetadata()) {
                this.metadata = this.type.createMetadata();
            }
        }
    }
    
    public IMetadata getMetadata() {
        return metadata;
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
