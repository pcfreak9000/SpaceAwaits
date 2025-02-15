package mod;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.ecs.content.Tickable;
import de.pcfreak9000.spaceawaits.serialize.NBTSerialize;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.ITileEntity;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class LaserTileEntity implements Tickable, ITileEntity {
    
    @NBTSerialize(key = "progress")
    private float progress = 0;
    
    private World world;
    
    private int gtx, gty;
    
    public LaserTileEntity(World w, int gtx, int gty) {
        this.world = w;
        this.gtx = gtx;
        this.gty = gty;
    }
    
    @Override
    public void tick(float time, long tick) {
        progress += time;
        if (gty - progress >= 0 && progress >= 1) {
            if (world.getSystem(TileSystem.class).getTile(gtx, gty - Mathf.floori(progress), TileLayer.Front)
                    .canBreak()) {
                world.getSystem(TileSystem.class).setTile(gtx, gty - Mathf.floori(progress), TileLayer.Front,
                        Tile.NOTHING);
            }
        } else if (gty - progress < 0) {
            progress = 0;
        }
    }
    
}
