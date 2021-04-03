package de.pcfreak9000.spaceawaits.world.tile;

import java.util.Objects;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TileState implements Poolable {
    
    private static final Pool<TileState> TILESTATE_POOL = new Pool<TileState>() {
        @Override
        protected TileState newObject() {
            return new TileState();
        }
    };
    
    public static TileState get(Tile t, int gtx, int gty) {
        TileState s = TILESTATE_POOL.obtain();
        s.initTileState(t, gtx, gty);
        return s;
    }
    
    public static void free(TileState s) {
        TILESTATE_POOL.free(s);
    }
    
    private int globalTileX;
    private int globalTileY;
    
    private Tile type;
    
    private Fixture fixture;
    private TileEntity tileEntity = null;
    
    private void initTileState(Tile type, int gtx, int gty) {
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
    public void reset() {
        this.type = Tile.EMPTY;
        this.fixture = null;
        this.tileEntity = null;
    }
    
    @Override
    public String toString() {
        return String.format("Tile[%s, x=%d, y=%d]", this.type, this.globalTileX, this.globalTileY);
    }
}
