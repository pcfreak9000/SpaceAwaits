package de.pcfreak9000.spaceawaits.world2;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.badlogic.ashley.core.EntitySystem;

import de.pcfreak9000.spaceawaits.world.ChunkCoordinateKey;
import de.pcfreak9000.spaceawaits.world.tile.Chunk;

public class TicketedChunkManager extends EntitySystem {
    
    private World world;
    private ChunkProvider chunkProvider;
    
    private Set<ITicket> tickets;
    
    private Set<ChunkCoordinateKey> chunksPrev;
    private Set<ChunkCoordinateKey> chunksToUpdate = new LinkedHashSet<>();
    private Set<ChunkCoordinateKey> chunksToLoad = new LinkedHashSet<>();
    
    public TicketedChunkManager(World world, ChunkProvider chunkprovider) {
        this.tickets = new LinkedHashSet<>();
        this.chunksPrev = new LinkedHashSet<>();
        this.world = world;
        this.chunkProvider = chunkprovider;
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
                t.update(dt);
            }
            if (!t.isValid()) {
                it.remove();
            }
            ChunkCoordinates[] chunks = t.getLoadChunks();
            for (ChunkCoordinates cc : chunks) {
                chunksToUpdate.add(cc.createKey());
            }
        }
        for (ChunkCoordinateKey up : chunksToUpdate) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    ChunkCoordinateKey load = new ChunkCoordinateKey(up.getX() + i, up.getY() + j);
                    if (!chunksToUpdate.contains(load)) {
                        chunksToLoad.add(load);
                    }
                }
            }
        }
        for (ChunkCoordinateKey k : chunksPrev) {
            if (!chunksToUpdate.contains(k)) {
                Chunk c = this.chunkProvider.getChunk(k.getX(), k.getY());
                world.removeChunk(c);
                if (!chunksToLoad.contains(k)) {
                    this.chunkProvider.queueUnloadChunk(k.getX(), k.getY());
                }
            }
        }
        for (ChunkCoordinateKey k : chunksToLoad) {
            this.chunkProvider.loadChunk(k.getX(), k.getY());
        }
        for (ChunkCoordinateKey k : chunksToUpdate) {
            Chunk c = this.chunkProvider.loadChunk(k.getX(), k.getY());
            if (c != null) { //is null if out of bounds, maybe just check beforehand for that?
                world.addChunk(c);
            }
        }
        chunksPrev.clear();
        chunksPrev.addAll(chunksToLoad);
        chunksPrev.addAll(chunksToUpdate);
    }

}
