package de.pcfreak9000.spaceawaits.world.ecs;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.LongMap.Keys;

import de.pcfreak9000.spaceawaits.util.IntCoords;
import de.pcfreak9000.spaceawaits.world.IChunkProvider;
import de.pcfreak9000.spaceawaits.world.ITicket;
import de.pcfreak9000.spaceawaits.world.World;

public class TicketedChunkManager extends EntitySystem {
    
    private World world;
    private IChunkProvider chunkProvider;
    
    private Set<ITicket> tickets;
    
    private static final Object OBJ = new Object();
    private LongMap<Object> chunksToUpdate = new LongMap<>();
    
    public TicketedChunkManager(World world, IChunkProvider chunkprovider) {
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
    }
    
}
