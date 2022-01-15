package de.pcfreak9000.spaceawaits.world;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.badlogic.ashley.core.EntitySystem;

import de.pcfreak9000.spaceawaits.util.IntCoordKey;
import de.pcfreak9000.spaceawaits.util.IntCoords;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

public class TicketedChunkManager extends EntitySystem {
    
    private World world;
    private ChunkStuff chunkStuff;
    
    private Set<ITicket> tickets;
    
    private Set<IntCoordKey> chunksPrev;
    private Set<IntCoordKey> chunksToUpdate = new LinkedHashSet<>();
    private Set<IntCoordKey> chunksToLoad = new LinkedHashSet<>();
    
    public TicketedChunkManager(World world, ChunkStuff chunkprovider) {
        this.tickets = new LinkedHashSet<>();
        this.chunksPrev = new LinkedHashSet<>();
        this.world = world;
        this.chunkStuff = chunkprovider;
    }
    
    public void addTicket(ITicket t) {
        this.tickets.add(t);
    }
    
    public void removeTicket(ITicket t) {
        this.tickets.remove(t);
    }
    
    @Override
    public void update(float dt) {
        chunksToUpdate.clear();
        chunksToLoad.clear();
        Iterator<ITicket> it = tickets.iterator();
        while (it.hasNext()) {
            ITicket t = it.next();
            if (t.isValid()) {
                t.update(world, dt);
            }
            if (!t.isValid()) {
                it.remove();
            }
            IntCoords[] chunks = t.getLoadChunks();
            for (IntCoords cc : chunks) {
                if (world.getBounds().inChunkBounds(cc.getX(), cc.getY())) {
                    chunksToUpdate.add(cc.createKey());
                }
            }
        }
        //find bordering chunks. load them, but dont update them.
        final int borderingChunkRad = 1;
        for (IntCoordKey up : chunksToUpdate) {
            for (int i = -borderingChunkRad; i <= borderingChunkRad; i++) {
                for (int j = -borderingChunkRad; j <= borderingChunkRad; j++) {
                    if (world.getBounds().inChunkBounds(up.getX() + i, up.getY() + j) && (i != 0 || j != 0)) {
                        IntCoordKey load = new IntCoordKey(up.getX() + i, up.getY() + j);
                        if (!chunksToUpdate.contains(load)) {
                            chunksToLoad.add(load);
                        }
                    }
                }
            }
        }
        //Find chunks which aren't needed anymore and unload them
        for (IntCoordKey k : chunksPrev) {
            if (!chunksToUpdate.contains(k)) {
                Chunk c = this.chunkStuff.getChunk(k.getX(), k.getY());
                if (!chunksToLoad.contains(k)) {
                    this.chunkStuff.releaseChunk(k.getX(), k.getY(), this);
                    //this.chunkProvider.queueUnloadChunk(k.getX(), k.getY());
                }
            }
        }
        //load bordering chunks
        for (IntCoordKey k : chunksToLoad) {
            this.chunkStuff.requireChunk(k.getX(), k.getY(), false, this);
        }
        //load and activate chunks to update
        for (IntCoordKey k : chunksToUpdate) {
            this.chunkStuff.requireChunk(k.getX(), k.getY(), true, this);
        }
        chunksPrev.clear();
        chunksPrev.addAll(chunksToLoad);
        chunksPrev.addAll(chunksToUpdate);
    }
    
}
