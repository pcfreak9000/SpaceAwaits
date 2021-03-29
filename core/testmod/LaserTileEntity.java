import de.omnikryptec.math.Mathf;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;
import de.pcfreak9000.spaceawaits.world.WorldAccessor;
import de.pcfreak9000.spaceawaits.world.tile.Chunk;
import de.pcfreak9000.spaceawaits.world.tile.Tickable;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.TileEntity;
import de.pcfreak9000.spaceawaits.world.tile.TileState;

public class LaserTileEntity extends TileEntity implements Tickable, NBTSerializable {
    
    private float progress = 0;
    
    private TileState myState;
    private WorldAccessor world;
    
    public LaserTileEntity(WorldAccessor w, TileState state) {
        this.world = w;
        this.myState = state;
    }
    
    @Override
    public void tick(float time) {
        progress += time;
        if (myState.getGlobalTileY() - progress >= 0 && progress >= 1) {
            Chunk tw = world.getChunk(Chunk.toGlobalChunk(myState.getGlobalTileX()),
                    Chunk.toGlobalChunk(myState.getGlobalTileY() - Mathf.floori(progress)));
            if (tw != null && tw.getTile(myState.getGlobalTileX(), myState.getGlobalTileY() - Mathf.floori(progress))
                    .canBreak()) {
                tw.setTile(Tile.EMPTY, myState.getGlobalTileX(), myState.getGlobalTileY() - Mathf.floori(progress));
            }
        } else if (myState.getGlobalTileY() - progress < 0) {
            progress = 0;
        }
    }
    
    @Override
    public void readNBT(NBTTag tag) {
        NBTTag.FloatEntry e = (NBTTag.FloatEntry) tag;
        this.progress = e.getFloat();
    }
    
    @Override
    public NBTTag writeNBT() {
        return new NBTTag.FloatEntry(progress);
    }
}
