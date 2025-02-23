package de.pcfreak9000.spaceawaits.world.chunk.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.pcfreak9000.spaceawaits.core.ecs.Transferable;
import de.pcfreak9000.spaceawaits.core.ecs.Saveable;
import de.pcfreak9000.spaceawaits.core.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.world.IChunkLoader;
import de.pcfreak9000.spaceawaits.world.IWorldProperties;
import de.pcfreak9000.spaceawaits.world.WorldBounds;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.mgmt.FollowingTicket;
import de.pcfreak9000.spaceawaits.world.chunk.mgmt.ITicket;
import de.pcfreak9000.spaceawaits.world.chunk.mgmt.IWorldChunkProvider;
import de.pcfreak9000.spaceawaits.world.chunk.mgmt.TestChunkProvider;
import de.pcfreak9000.spaceawaits.world.chunk.mgmt.TicketedChunkManager;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.gen.IChunkGenerator;
import de.pcfreak9000.spaceawaits.world.tile.ChunkECSHandler;

public class ChunkSystem extends IteratingSystem implements ChunkECSHandler, Transferable, Saveable {

    private class TicketedEntityListener implements EntityListener {

        @Override
        public void entityAdded(Entity arg0) {
            ChunkTicketComponent ctc = Components.CHUNK_TICKET.get(arg0);
            TransformComponent tc = Components.TRANSFORM.get(arg0);
            ctc.currentTicket = new FollowingTicket(tc.position, ctc.radius);
            ChunkSystem.this.addTicket(ctc.currentTicket);
        }

        @Override
        public void entityRemoved(Entity arg0) {
            ChunkTicketComponent ctc = Components.CHUNK_TICKET.get(arg0);
            ChunkSystem.this.removeTicket(ctc.currentTicket);
            ctc.currentTicket = null;
        }

    }

    private final IWorldChunkProvider chunkProvider;
    private final IChunkLoader chunkLoader;
    private TicketedChunkManager chunkManager;

    private TicketedEntityListener listener;

    private int countChunkActive = 0;

    private WorldBounds bounds;

    public ChunkSystem(IChunkLoader chunkloader, IChunkGenerator gen, WorldBounds bounds, IWorldProperties props) {
        super(Family.all(ChunkComponent.class, TransformComponent.class).get());
        this.bounds = bounds;
        this.listener = new TicketedEntityListener();
        this.chunkLoader = chunkloader;// Save <-> ChunkProvider
        this.chunkProvider = new TestChunkProvider(bounds, chunkLoader, gen, this, props); // ChunkProvider/Generator
                                                                                           // <-> World
        this.chunkManager = new TicketedChunkManager(bounds, chunkProvider); // Managed chunks
    }

    public Chunk getChunk(int i, int j) {
        if (!bounds.inBoundsChunk(i, j)) {
            return null;
        }
        return chunkProvider.getChunk(i, j);
    }

    public int getUpdatingChunksCount() {
        return countChunkActive;
    }

    public int getLoadedChunksCount() {
        return chunkProvider.getLoadedChunkCount();
    }

    public void addTicket(ITicket ticket) {
        this.chunkManager.addTicket(ticket);
    }

    public void removeTicket(ITicket ticket) {
        this.chunkManager.removeTicket(ticket);
    }

    public void adjustChunk(Entity e, ChunkComponent c, TransformComponent t) {
        int supposedChunkX = Chunk.toGlobalChunkf(t.position.x);
        int supposedChunkY = Chunk.toGlobalChunkf(t.position.y);
        if (c.currentChunk == null) {
            throw new NullPointerException();
        } else if (supposedChunkX != c.currentChunk.getGlobalChunkX()
                || supposedChunkY != c.currentChunk.getGlobalChunkY()) {
            Chunk newchunk = chunkProvider.getChunk(supposedChunkX, supposedChunkY);
            // If for some reason the new chunk doesn't exist, keep the old link
            if (newchunk != null) {
                c.currentChunk.removeEntityStatic(e);
                newchunk.addEntityStatic(e);
                if (!newchunk.isActive()) {
                    // Calling this method means the supplied entity is updating, but after the
                    // switch it might not be supposed to be anymore so it will be removed
                    getEngine().removeEntity(e);
                }
            }
        }
    }

    @Override
    public void unload() {
        this.chunkProvider.unloadAll();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        this.chunkManager.update();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent tc = Components.TRANSFORM.get(entity);
        ChunkComponent mwec = Components.CHUNK.get(entity);
        adjustChunk(entity, mwec, tc);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(Family.all(TransformComponent.class, ChunkTicketComponent.class).get(), listener);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        engine.removeEntityListener(listener);
    }

    @Override
    public void addChunk(Chunk c) {
        c.addToECS(getEngine());
        countChunkActive++;
    }

    @Override
    public void removeChunk(Chunk c) {
        c.removeFromECS();
        countChunkActive--;
    }

    @Override
    public void load() {
        // TODO use this to preload chunks around the player??
    }

    @Override
    public void save() {
        chunkLoader.saveAllChunks();
    }

}
