package de.pcfreak9000.spaceawaits.world.ecs.content;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.badlogic.ashley.core.EntitySystem;

import de.pcfreak9000.spaceawaits.util.IntCoordKey;
import de.pcfreak9000.spaceawaits.util.IntCoords;
import de.pcfreak9000.spaceawaits.world.ChunkProvider;
import de.pcfreak9000.spaceawaits.world.ITicket;
import de.pcfreak9000.spaceawaits.world.World;

public class TicketedChunkManager extends EntitySystem {
    
    private World world;
    private ChunkProvider chunkProvider;
    
    private Set<ITicket> tickets;
    
    private Set<IntCoordKey> chunksToUpdate = new LinkedHashSet<>();
    
    public TicketedChunkManager(World world, ChunkProvider chunkprovider) {
        this.tickets = new LinkedHashSet<>();
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
                if (world.getBounds().inBoundsChunk(cc.getX(), cc.getY())) {
                    chunksToUpdate.add(cc.createKey());
                }
            }
        }
        //load and activate chunks to update. chunks not needed are handled by the ChunkProvider. 
        //"Bordering" chunks aren't needed, chunks are loaded if needed, but usually only in the in-active state. 
        for (IntCoordKey k : chunksToUpdate) {
            this.chunkProvider.getChunk(k.getX(), k.getY(), true);
        }
    }
    
}
