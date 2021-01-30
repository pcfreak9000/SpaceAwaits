package de.pcfreak9000.spaceawaits.tileworld;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.pcfreak9000.spaceawaits.tileworld.tile.Chunk;

/**
 * Responsible for successful surface world loading and unloading, management of
 * loaded chunks
 *
 * @author pcfreak9000
 *
 */
public class WorldLoader {
    
    private WorldManager manager;
    
    private World currentWorld;
    private WorldLoadingBounds worldLoadingBounds;
    
    //TODO recache loaded regions if resources have been reloaded
    
    private final Set<Chunk> localLoadedChunks;
    
    public WorldLoader(WorldManager worldMgr) {
        this.localLoadedChunks = new HashSet<>();
        this.manager = worldMgr;
    }
    
    public void setWorld(World w) {
        if (hasCurrentWorld()) {
            unloadAllRegions();
            if (this.currentWorld.getBackground() != null) {
                manager.getECSManager().removeEntity(this.currentWorld.getBackground().getEntity());
            }
        }
        this.currentWorld = w;
        if (hasCurrentWorld()) {
            loadAllRegions();
            if (this.currentWorld.getBackground() != null) {
                manager.getECSManager().addEntity(this.currentWorld.getBackground().getEntity());
            }
        }
    }
    
    private boolean hasCurrentWorld() {
        return this.currentWorld != null;
    }
    
    public void setWorldUpdateFence(WorldLoadingBounds fence) {
        if (!hasCurrentWorld()) {
            this.worldLoadingBounds = fence;
            return;
        }
        unloadAllRegions();
        this.worldLoadingBounds = fence;
        loadAllRegions();
    }
    
    private void loadAllRegions() {
        int xR = this.worldLoadingBounds.getChunkRadiusRangeX();
        int yR = this.worldLoadingBounds.getChunkRadiusRangeY();
        int xM = this.worldLoadingBounds.getChunkMidpointX();
        int yM = this.worldLoadingBounds.getChunkMidpointY();
        for (int i = 0; i <= 2 * xR; i++) {
            for (int j = 0; j <= 2 * yR; j++) {
                int rx = i - xR + xM;
                int ry = j - yR + yM;
                if (this.currentWorld.getTileWorld().getMeta().inChunkBounds(rx, ry)) {
                    Chunk c = this.currentWorld.getTileWorld().requestRegion(rx, ry);
                    if (c != null) {
                        this.localLoadedChunks.add(c);
                        manager.getECSManager().addEntity(c.getECSEntity());
                    }
                }
            }
        }
    }
    
    private void unloadAllRegions() {
        Iterator<Chunk> it = this.localLoadedChunks.iterator();
        while (it.hasNext()) {
            Chunk c = it.next();
            manager.getECSManager().removeEntity(c.getECSEntity());
            it.remove();
        }
    }
    
    //make sure that the chunks are updated for dynamics after the movement but before this
    public void loadChunks(float dtime) {
        //TODO improve world chunk loading update -> unload all non-needed in update and load new needed
        unloadAllRegions();
        loadAllRegions();
        //System.out.println(time.ops);
    }
}
