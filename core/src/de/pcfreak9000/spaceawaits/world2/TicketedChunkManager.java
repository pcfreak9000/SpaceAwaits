package de.pcfreak9000.spaceawaits.world2;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.badlogic.ashley.core.EntitySystem;

import de.pcfreak9000.spaceawaits.world.ChunkCoordinateKey;

public class TicketedChunkManager extends EntitySystem {
    
    private World world;
    private IChunkProvider chunkProvider;
    
    private Set<ITicket> tickets;
    
    private Set<ChunkCoordinateKey> chunksToUpdate;
    
    public TicketedChunkManager(World world, IChunkProvider chunkprovider) {
        this.tickets = new LinkedHashSet<>();
        this.chunksToUpdate = new LinkedHashSet<>();
        this.world = world;
        this.chunkProvider = chunkprovider;
    }
    
    public void addTicket(ITicket t) {
        this.tickets.add(t);
    }
    
    public void removeTicket(ITicket t) {
        this.tickets.remove(t);
    }
    
    public void update(float dt) {
        chunksToUpdate.clear();
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
    }
    
}
