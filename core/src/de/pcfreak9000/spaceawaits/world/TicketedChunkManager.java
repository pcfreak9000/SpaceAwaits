package de.pcfreak9000.spaceawaits.world;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.badlogic.ashley.core.EntitySystem;

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
                t.update(world, dt);
            }
            if (!t.isValid()) {
                it.remove();
            }
            ChunkCoordinates[] chunks = t.getLoadChunks();
            for (ChunkCoordinates cc : chunks) {
                if (world.getBounds().inChunkBounds(cc.getChunkX(), cc.getChunkY())) {
                    chunksToUpdate.add(cc.createKey());
                }
            }
        }
        //find bordering chunks. load them, but dont update them.
        final int borderingChunkRad = 1;
        for (ChunkCoordinateKey up : chunksToUpdate) {
            for (int i = -borderingChunkRad; i <= borderingChunkRad; i++) {
                for (int j = -borderingChunkRad; j <= borderingChunkRad; j++) {
                    if (world.getBounds().inChunkBounds(up.getX() + i, up.getY() + j) && (i != 0 || j != 0)) {
                        ChunkCoordinateKey load = new ChunkCoordinateKey(up.getX() + i, up.getY() + j);
                        if (!chunksToUpdate.contains(load)) {
                            chunksToLoad.add(load);
                        }
                    }
                }
            }
        }
        //Find chunks which aren't needed anymore and unload them
        for (ChunkCoordinateKey k : chunksPrev) {
            if (!chunksToUpdate.contains(k)) {
                Chunk c = this.chunkProvider.getChunk(k.getX(), k.getY());
                if (c.isActive()) {
                    world.removeChunk(c);
                }
                if (!chunksToLoad.contains(k)) {
                    this.chunkProvider.queueUnloadChunk(k.getX(), k.getY());
                }
            }
        }
        //load bordering chunks
        for (ChunkCoordinateKey k : chunksToLoad) {
            this.chunkProvider.loadChunk(k.getX(), k.getY());
        }
        //load and activate chunks to update
        for (ChunkCoordinateKey k : chunksToUpdate) {
            Chunk c = this.chunkProvider.loadChunk(k.getX(), k.getY());
            if (!c.isActive()) {
                world.addChunk(c);
            }
        }
        chunksPrev.clear();
        chunksPrev.addAll(chunksToLoad);
        chunksPrev.addAll(chunksToUpdate);
    }
    
}
