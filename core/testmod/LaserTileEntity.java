import de.omnikryptec.math.Mathf;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;
import de.pcfreak9000.spaceawaits.world.WorldAccessor;
import de.pcfreak9000.spaceawaits.world.tile.Chunk;
import de.pcfreak9000.spaceawaits.world.tile.Tickable;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.TileEntity;

public class LaserTileEntity extends TileEntity implements Tickable, NBTSerializable {
    
    private float progress = 0;
    
    private WorldAccessor world;
    
    private int gtx, gty;
    
    public LaserTileEntity(WorldAccessor w, int gtx, int gty) {
        this.world = w;
        this.gtx = gtx;
        this.gty = gty;
    }
    
    @Override
    public void tick(float time) {
        progress += time;
        if (gty - progress >= 0 && progress >= 1) {
            Chunk tw = world.getChunk(Chunk.toGlobalChunk(gtx), Chunk.toGlobalChunk(gty - Mathf.floori(progress)));
            if (tw != null && tw.getTile(gtx, gty - Mathf.floori(progress)).canBreak()) {
                tw.setTile(Tile.EMPTY, gtx, gty - Mathf.floori(progress));
            }
        } else if (gty - progress < 0) {
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
