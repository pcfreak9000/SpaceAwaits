package de.pcfreak9000.spaceawaits.world.chunk.mgmt;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.LongMap.Keys;

import de.pcfreak9000.spaceawaits.util.IntCoords;
import de.pcfreak9000.spaceawaits.world.WorldBounds;

public class TicketedChunkManager {
    
    private WorldBounds bounds;
    private IWorldChunkProvider chunkProvider;
    
    private Set<ITicket> tickets;
    
    private static final Object OBJ = new Object();
    private LongMap<Object> chunksToUpdate = new LongMap<>();
    
    public TicketedChunkManager(WorldBounds bounds, IWorldChunkProvider chunkprovider) {
        this.tickets = new LinkedHashSet<>();
        this.bounds = bounds;
        this.chunkProvider = chunkprovider;
    }
    
    public void addTicket(ITicket t) {
        this.tickets.add(t);
    }
    
    public void removeTicket(ITicket t) {
        this.tickets.remove(t);
    }
    
    public void update() {
        chunksToUpdate.clear();
        Iterator<ITicket> it = tickets.iterator();
        while (it.hasNext()) {
            ITicket t = it.next();
            if (t.isValid()) {
                t.update();
            }
            if (!t.isValid()) {
                it.remove();
            }
            IntCoords[] chunks = t.getLoadChunks();
            for (IntCoords cc : chunks) {
                if (bounds.inBoundsChunk(cc.getX(), cc.getY())) {
                    chunksToUpdate.put(cc.createKey(), OBJ);
                }
            }
        }
        //load and activate chunks to update. chunks not needed are handled by the ChunkProvider. 
        //"Bordering" chunks aren't needed, chunks are loaded if needed, but usually only in the in-active state. 
        Keys keys = chunksToUpdate.keys();
        while (keys.hasNext) {
            long k = keys.next();
            this.chunkProvider.requestChunk(IntCoords.xOfLong(k), IntCoords.yOfLong(k), true);
        }
        this.chunkProvider.flush();
    }
    
}
