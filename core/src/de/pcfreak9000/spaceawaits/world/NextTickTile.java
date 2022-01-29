package de.pcfreak9000.spaceawaits.world;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class NextTickTile implements NBTSerializable {
    private int x;
    private int y;
    private TileLayer layer;
    private Tile tile;
    private int tick;
    
    public NextTickTile(int x, int y, TileLayer layer, Tile tile, int tick) {
        this.x = x;
        this.y = y;
        this.layer = layer;
        this.tile = tile;
        this.tick = tick;
    }
    
    public int getTick() {
        return tick;
    }
    
    public Tile getTile() {
        return this.tile;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public TileLayer getLayer() {
        return layer;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((layer == null) ? 0 : layer.hashCode());
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + tick;
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NextTickTile)) {
            return false;
        }
        NextTickTile other = (NextTickTile) obj;
        if (layer != other.layer) {
            return false;
        }
        if (x != other.x) {
            return false;
        }
        if (y != other.y) {
            return false;
        }
        if (tick != other.tick) {
            return false;
        }
        return true;
    }
    
    @Override
    public void readNBT(NBTTag tag) {
        NBTCompound c = (NBTCompound) tag;
        x = c.getInt("x");
        y = c.getInt("y");
        layer = c.getByte("z") == 1 ? TileLayer.Front : TileLayer.Back;
        tile = GameRegistry.TILE_REGISTRY.get(c.getString("t"));
        tick = c.getInt("tick");
    }
    
    @Override
    public NBTTag writeNBT() {
        NBTCompound c = new NBTCompound();
        c.putInt("x", x);
        c.putInt("y", y);
        c.putByte("z", layer == TileLayer.Front ? (byte) 1 : (byte) 0);
        c.putString("t", GameRegistry.TILE_REGISTRY.getId(tile));
        c.putInt("tick", tick);
        return c;
    }
    
}
