package de.pcfreak9000.spaceawaits.tileworld;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.collections4.set.ListOrderedSet;

import com.badlogic.ashley.core.Engine;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.tileworld.light.AmbientLightProvider;
import de.pcfreak9000.spaceawaits.tileworld.tile.Chunk;
import de.pcfreak9000.spaceawaits.tileworld.tile.Tile;
import de.pcfreak9000.spaceawaits.tileworld.tile.TileState;
import de.pcfreak9000.spaceawaits.tileworld.tile.WorldMeta;
import de.pcfreak9000.spaceawaits.tileworld.tile.WorldProvider;

public class WorldAccessor {
    private static final int LOAD_OFFSET = 2;
    
    private static String coordsToString(int gcx, int gcy) {
        return gcx + ":" + gcy;
    }
    
    //Entities in the Chunks?
    //What about force loading?
    //Keep often visited chunks loaded?
    
    //NICE! Forgot that there can be multiple worlds... FUCK -> multiple "Loading" i guess for the server
    
    //Map: loaded chunks
    private Map<String, Chunk> chunksLoaded;
    //Map: updated chunks
    private Map<String, Chunk> chunksUpdated;
    //(Map: rendering chunks?) -> ChunkRenderComponent involvement?
    
    private Set<String> requested;
    private Queue<Chunk> fulfilledRequested;
    
    //WorldProvider or something, abstract, handles loading chunks from the network, from files or generating them (mind the often asynchronous nature though)
    private WorldProvider worldProvider;
    
    //Client: Keep track of the local player, load/unload chunks
    //Server: Keep track of all players, load/unload chunks
    private Set<WorldLoadingBounds> bounds;
    
    private WorldManager wmgr;
    
    public WorldAccessor(WorldManager wmgr) {
        this.chunksLoaded = new ListOrderedMap<>();
        this.chunksUpdated = new ListOrderedMap<>();
        this.requested = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.fulfilledRequested = new ConcurrentLinkedQueue<>();
        this.bounds = new ListOrderedSet<>();
        this.wmgr = wmgr;
    }
    
    /**
     * Change the chunks states or load/unload them if required
     */
    public void unloadload() {
        downgrade();
        upgrade();
    }
    
    public Tile getTile(int tx, int ty) {
        int rx = Chunk.toGlobalChunk(tx);
        int ry = Chunk.toGlobalChunk(ty);
        Chunk r = getChunk(rx, ry);
        return r == null ? null : r.getTile(tx, ty);//Meh
    }
    
    public Tile getTileBackground(int tx, int ty) {
        int rx = Chunk.toGlobalChunk(tx);
        int ry = Chunk.toGlobalChunk(ty);
        Chunk r = getChunk(rx, ry);
        return r == null ? null : r.getBackground(tx, ty);//Meh
    }
    
    public void setTile(Tile tile, int tx, int ty) {
        int rx = Chunk.toGlobalChunk(tx);
        int ry = Chunk.toGlobalChunk(ty);
        Chunk r = getChunk(rx, ry);
        if (r != null) {
            r.setTile(tile, tx, ty);
        }
    }
    
    public void setTileBackground(Tile tile, int tx, int ty) {
        int rx = Chunk.toGlobalChunk(tx);
        int ry = Chunk.toGlobalChunk(ty);
        Chunk r = getChunk(rx, ry);
        if (r != null) {
            r.setTileBackground(tile, tx, ty);
        }
    }
    
    public Chunk getChunk(int gcx, int gcy) {
        String sc = coordsToString(gcx, gcy);
        Chunk c = null;
        c = chunksLoaded.get(sc);
        if (c == null) {
            c = chunksUpdated.get(sc);
        }
        return c;
    }
    
    public void collectTileIntersections(Collection<TileState> output, int x, int y, int w, int h,
            Predicate<TileState> predicate) {
        boolean xy = worldProvider.getMeta().inBounds(x, y);
        boolean xwyh = worldProvider.getMeta().inBounds(x + w, y + h);
        if (!xy && !xwyh) {
            return;
        }
        Set<Chunk> chunks = new HashSet<>();
        if (xy) {
            Chunk reg = getChunk(Chunk.toGlobalChunk(x), Chunk.toGlobalChunk(y));
            if (reg != null) {
                chunks.add(reg);
            }
        }
        if (xwyh) {
            Chunk reg = getChunk(Chunk.toGlobalChunk(x + w), Chunk.toGlobalChunk(y + h));
            if (reg != null) {
                chunks.add(reg);
            }
        }
        if (worldProvider.getMeta().inBounds(x + w, y)) {
            Chunk reg = getChunk(Chunk.toGlobalChunk(x + w), Chunk.toGlobalChunk(y));
            if (reg != null) {
                chunks.add(reg);
            }
        }
        if (worldProvider.getMeta().inBounds(x, y + h)) {
            Chunk reg = getChunk(Chunk.toGlobalChunk(x), Chunk.toGlobalChunk(y + h));
            if (reg != null) {
                chunks.add(reg);
            }
        }
        for (Chunk r : chunks) {
            r.tileIntersections(output, x, y, w, h, predicate);
        }
    }
    
    public void setWorldProvider(WorldProvider wp) {//TMP?
        if (wp != this.worldProvider) {
            if (this.worldProvider != null) {
                wmgr.getECSManager().removeEntity(worldProvider.getBackground().getEntity());
            }
            SpaceAwaits.BUS.post(new WorldEvents.SetWorldEvent(this.wmgr, this.worldProvider, wp));
            this.worldProvider = wp;
            if (this.worldProvider != null) {
                wmgr.getECSManager().addEntity(worldProvider.getBackground().getEntity());
            }
        }
    }
    
    public void addLoadingBounds(WorldLoadingBounds wlb) {
        this.bounds.add(wlb);
    }
    
    public void removeLoadingBounds(WorldLoadingBounds wlb) {
        this.bounds.remove(wlb);
    }
    
    private void downgrade() {
        //Find updating chunks to move down to loaded (-> loaded, unload)
        for (Iterator<Entry<String, Chunk>> it = chunksUpdated.entrySet().iterator(); it.hasNext();) {
            Entry<String, Chunk> e = it.next();
            Chunk c = e.getValue();
            if (!inLoadingRange(c.getGlobalChunkX(), c.getGlobalChunkY())) {
                removeChunkFromSystem(c);
                unload(c);
                it.remove();
            } else if (!inUpdateRange(c.getGlobalChunkX(), c.getGlobalChunkY())) {
                addLoaded(e.getKey(), e.getValue());
                removeChunkFromSystem(c);
                it.remove();
            }
        }
        //Find loaded chunks to unload (-> unload)
        for (Iterator<Entry<String, Chunk>> it = chunksLoaded.entrySet().iterator(); it.hasNext();) {
            Entry<String, Chunk> e = it.next();
            Chunk c = e.getValue();
            if (!inLoadingRange(c.getGlobalChunkX(), c.getGlobalChunkY())) {
                unload(c);
                it.remove();
            }
        }
    }
    
    private void upgrade() {
        //Find unloaded chunks to request (-> loaded, updated)
        requestNewChunks();
        while (!fulfilledRequested.isEmpty()) {
            Chunk c = fulfilledRequested.poll();
            if (c != null) {//Shouldn't be false, but multithreading is weird sometimes
                String sc = coordsToString(c.getGlobalChunkX(), c.getGlobalChunkY());
                requested.remove(sc);
                if (this.chunksLoaded.containsKey(sc) || this.chunksUpdated.containsKey(sc)) {
                    continue;//Requested chunk is already loaded
                }
                if (inLoadingRange(c.getGlobalChunkX(), c.getGlobalChunkY())) {
                    if (inUpdateRange(c.getGlobalChunkX(), c.getGlobalChunkY())) {
                        addUpdated(sc, c);
                    } else {
                        addLoaded(sc, c);
                    }
                }
            }
        }
        //Find loaded chunks to update (-> updated)
        for (Iterator<Entry<String, Chunk>> it = chunksLoaded.entrySet().iterator(); it.hasNext();) {
            Entry<String, Chunk> e = it.next();
            Chunk c = e.getValue();
            if (inUpdateRange(c.getGlobalChunkX(), c.getGlobalChunkY())) {
                addUpdated(e.getKey(), e.getValue());
                it.remove();
            }
        }
    }
    
    private void unload(Chunk c) {
        this.worldProvider.unload(c);
    }
    
    private void addLoaded(String sc, Chunk c) {
        chunksLoaded.put(sc, c);
    }
    
    private void addUpdated(String sc, Chunk c) {
        chunksUpdated.put(sc, c);
        addChunkToSystem(c);
    }
    
    private void addChunkToSystem(Chunk c) {
        Engine ecs = wmgr.getECSManager();
        ecs.addEntity(c.getECSEntity());
    }
    
    private void removeChunkFromSystem(Chunk c) {
        Engine ecs = wmgr.getECSManager();
        ecs.removeEntity(c.getECSEntity());
    }
    
    private void requestChunk(int gcx, int gcy) {
        String sc = coordsToString(gcx, gcy);
        if (!requested.contains(sc) && !this.chunksLoaded.containsKey(sc) && !this.chunksUpdated.containsKey(sc)) {
            requested.add(sc);
            this.worldProvider.requestChunk(gcx, gcy, (c) -> {
                fulfilledRequested.add(c);
            });
        }
    }
    
    private void requestNewChunks() {
        for (WorldLoadingBounds wlb : bounds) {
            int xrad = LOAD_OFFSET + wlb.getChunkRadiusRangeX();
            int yrad = LOAD_OFFSET + wlb.getChunkRadiusRangeY();
            for (int x = 0; x <= xrad * 2; x++) {
                for (int y = 0; y <= yrad * 2; y++) {
                    int gcx = wlb.getChunkMidpointX() + x - xrad;
                    int gcy = wlb.getChunkMidpointY() + y - yrad;
                    if (this.worldProvider.getMeta().inChunkBounds(gcx, gcy)) {
                        requestChunk(gcx, gcy);
                    }
                }
            }
        }
    }
    
    private boolean inUpdateRange(int gcx, int gcy) {
        for (WorldLoadingBounds wlb : bounds) {
            int minx = wlb.getChunkMidpointX() - wlb.getChunkRadiusRangeX();
            int miny = wlb.getChunkMidpointY() - wlb.getChunkRadiusRangeY();
            int maxx = wlb.getChunkMidpointX() + wlb.getChunkRadiusRangeX();
            int maxy = wlb.getChunkMidpointY() + wlb.getChunkRadiusRangeY();
            if (gcx >= minx && gcx <= maxx) {
                if (gcy >= miny && gcy <= maxy) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean inLoadingRange(int gcx, int gcy) {
        for (WorldLoadingBounds wlb : bounds) {
            int minx = wlb.getChunkMidpointX() - wlb.getChunkRadiusRangeX() - LOAD_OFFSET;
            int miny = wlb.getChunkMidpointY() - wlb.getChunkRadiusRangeY() - LOAD_OFFSET;
            int maxx = wlb.getChunkMidpointX() + wlb.getChunkRadiusRangeX() + LOAD_OFFSET;
            int maxy = wlb.getChunkMidpointY() + wlb.getChunkRadiusRangeY() + LOAD_OFFSET;
            if (gcx >= minx && gcx <= maxx) {
                if (gcy >= miny && gcy <= maxy) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public WorldMeta getMeta() {
        return worldProvider.getMeta();
    }
    
    public AmbientLightProvider getAmbientLight() {
        return worldProvider.getAmbientLight();
    }
}
