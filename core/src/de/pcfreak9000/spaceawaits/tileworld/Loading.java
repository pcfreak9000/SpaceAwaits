package de.pcfreak9000.spaceawaits.tileworld;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.collections4.set.ListOrderedSet;

import de.pcfreak9000.spaceawaits.tileworld.tile.Chunk;
import de.pcfreak9000.spaceawaits.tileworld.tile.WorldProvider;

public class Loading {
    
    private static String coordsToString(int gcx, int gcy) {
        return gcx + ":" + gcy;
    }
    //Set with loaded chunks
    //Set with updated chunks
    //Set with rendered chunks??? -> Server? Usefulness? Just loop through the loaded chunks and check if they need to be rendered somewhere?
    //Entitys in the Chunks?
    //Use a map instead with chunk coordinates? 
    //How to update the contents of the sets? -> PlayerMovementEvent? (oof because before collision... 
    //use event with result and query movement? quite oof actually) -> needs thoughts about events in general
    //Iterating through sets to find out about the individuals, meh -> additional Arrays for each step?
    
    //For networking, make World abstract or something? -> requestChunk
    
    //Map: loaded chunks
    private Map<String, Chunk> chunksLoaded;
    //Map: updated chunks
    private Map<String, Chunk> chunksUpdated;//Maybe instead use arraymaps?
    //(Map: rendering chunks?) -> ChunkRenderComponent involvement?
    
    private Set<String> requested;
    private Queue<Chunk> fulfilledRequested;
    
    //WorldProvider or something, abstract, handles loading chunks from the network, from files or generating them (mind the often asynchronous nature though)
    private WorldProvider worldProvider;
    
    //Client: Keep track of the local player, load/unload chunks
    //Server: Keep track of all players, load/unload chunks
    private Set<WorldLoadingBounds> bounds;
    
    //What about force loading?
    //Keep often visited chunks loaded?
    
    //NICE! Forgot that there can be multiple worlds... FUCK -> multiple "Loading" i guess for the server
    
    private WorldManager wmgr;
    
    public Loading(WorldManager wmgr) {
        this.chunksLoaded = new ListOrderedMap<>();
        this.chunksUpdated = new ListOrderedMap<>();
        this.requested = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.fulfilledRequested = new ConcurrentLinkedQueue<>();
        this.bounds = new ListOrderedSet<>();
        this.wmgr = wmgr;
    }
    
    public void unloadload() {
        downgrade();
        upgrade();
    }
    
    public void setWorldProvider(WorldProvider wp) {//TMP?
        this.worldProvider = wp;
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
                unload(c);
                it.remove();
            } else if (!inUpdateRange(c.getGlobalChunkX(), c.getGlobalChunkY())) {
                addLoaded(e.getKey(), e.getValue());
                //remove entity
                this.wmgr.getECSManager().removeEntity(c.getECSEntity());
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
                        //add entity
                        this.wmgr.getECSManager().addEntity(c.getECSEntity());
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
                //add entity
                this.wmgr.getECSManager().addEntity(c.getECSEntity());
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
            for (int x = 0; x <= wlb.getChunkRadiusRangeX() * 2; x++) {
                for (int y = 0; y <= wlb.getChunkRadiusRangeY() * 2; y++) {
                    int gcx = wlb.getChunkMidpointX() + x - wlb.getChunkRadiusRangeX();
                    int gcy = wlb.getChunkMidpointY() + y - wlb.getChunkRadiusRangeY();
                    if (this.worldProvider.getMeta().inChunkBounds(gcx, gcy)) {
                        requestChunk(gcx, gcy);
                    }
                }
            }
        }
    }
    
    private boolean inUpdateRange(int gcx, int gcy) {
        return true;
    }
    
    private boolean inLoadingRange(int gcx, int gcy) {
        return true;
    }
}
