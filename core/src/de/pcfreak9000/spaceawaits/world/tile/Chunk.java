package de.pcfreak9000.spaceawaits.world.tile;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Predicate;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.world.WorldAccessor;
import de.pcfreak9000.spaceawaits.world.ecs.chunk.ChunkComponent;
import de.pcfreak9000.spaceawaits.world.ecs.chunk.ChunkRenderComponent;
import de.pcfreak9000.spaceawaits.world.ecs.entity.ChunkMarkerComponent;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;

public class Chunk {
    
    private static final ComponentMapper<ChunkMarkerComponent> ChunkMarkerCompMapper = ComponentMapper
            .getFor(ChunkMarkerComponent.class); //Having ECS code in this class isn't entirely fancy either
    
    public static final int CHUNK_TILE_SIZE = 64;
    
    public static int toGlobalChunk(int globalTile) {
        return globalTile / CHUNK_TILE_SIZE; //<- This brings problems with negative numbers, but we dont use negative tile coordinates anyways
    }
    
    public static int toGlobalChunkf(float x) {
        return Mathf.floori(x / (CHUNK_TILE_SIZE * Tile.TILE_SIZE)); //well i hope this floor function works properly
    }
    
    private final int rx;
    private final int ry;
    
    private final int tx;
    private final int ty;
    
    private WorldAccessor worldAccessor;
    
    private final TileStorage tiles;
    private final TileStorage tilesBackground;
    private final List<TileEntity> tileEntities;
    private final List<Tickable> tickables;
    private final List<Entity> entities;
    private final List<Entity> immutableEntities;
    
    private final List<ChunkChangeListener> listeners;
    
    private final Queue<Tickable> tickablesForRemoval;
    private boolean ticking = false;
    
    private final Entity regionEntity;
    
    public Chunk(int rx, int ry, WorldAccessor worldAccessor) {
        this.rx = rx;
        this.ry = ry;
        this.tx = rx * CHUNK_TILE_SIZE;
        this.ty = ry * CHUNK_TILE_SIZE;
        this.listeners = new ArrayList<>();
        this.tiles = new TileStorage(CHUNK_TILE_SIZE, this.tx, this.ty);
        this.tilesBackground = new TileStorage(CHUNK_TILE_SIZE, this.tx, this.ty);
        this.tileEntities = new ArrayList<>();
        this.tickables = new ArrayList<>();
        this.entities = new ArrayList<>();
        this.immutableEntities = Collections.unmodifiableList(this.entities);
        this.tickablesForRemoval = new ArrayDeque<>();
        this.regionEntity = new Entity();
        this.regionEntity.add(new ChunkComponent(this));
        this.regionEntity.add(new ChunkRenderComponent());//TMP because server side stuff
        PhysicsComponent pc = new PhysicsComponent();
        pc.factory = new ChunkPhysics(this);
        this.regionEntity.add(pc);
        this.worldAccessor = worldAccessor;
    }
    
    private void notifyListeners(TileState newstate, TileState oldstate) {
        for (ChunkChangeListener l : listeners) {
            l.onTileStateChange(this, newstate, oldstate);
        }
    }
    
    public void addListener(ChunkChangeListener listener) {
        listeners.add(listener);
    }
    
    public int getGlobalChunkX() {
        return this.rx;
    }
    
    public int getGlobalChunkY() {
        return this.ry;
    }
    
    public int getGlobalTileX() {
        return this.tx;
    }
    
    public int getGlobalTileY() {
        return this.ty;
    }
    
    public Entity getECSEntity() {
        return regionEntity;
    }
    
    public void tileIntersections(Collection<TileState> output, int x, int y, int w, int h,
            Predicate<TileState> predicate) {
        this.tiles.getAABB(output, x, y, w, h, predicate);
    }
    
    public void tileIntersectionsBackground(Collection<TileState> output, int x, int y, int w, int h,
            Predicate<TileState> predicate) {
        this.tilesBackground.getAABB(output, x, y, w, h, predicate);
    }
    
    public void tileAll(Collection<TileState> output, Predicate<TileState> predicate) {
        tiles.getAll(output, predicate);
    }
    
    public void tileBackgroundAll(Collection<TileState> output, Predicate<TileState> predicate) {
        tilesBackground.getAll(output, predicate);
    }
    
    public Tile getTile(int tx, int ty) {
        return this.tiles.get(tx, ty).getTile();
    }
    
    public TileState getTileState(int tx, int ty) {//TODO not public...
        return this.tiles.get(tx, ty);
    }
    
    //Maybe save the set for later somehow? 
    
    public Tile setTile(Tile t, int tx, int ty) {
        Objects.requireNonNull(t);
        GameRegistry.TILE_REGISTRY.checkRegistered(t);
        TileState newTileState = new TileState(t, tx, ty);
        TileState old = this.tiles.set(newTileState, tx, ty);
        if (old.getTileEntity() != null) {
            this.tileEntities.remove(old.getTileEntity());
            if (old.getTileEntity() instanceof Tickable) {
                Tickable oldTickable = (Tickable) old.getTileEntity();
                if (ticking) {
                    tickablesForRemoval.add(oldTickable);
                } else {
                    tickables.remove(oldTickable);
                }
            }
            old.setTileEntity(null);
        }
        if (t.hasTileEntity()) {
            TileEntity te = t.createTileEntity(worldAccessor, newTileState);
            this.tileEntities.add(te);
            newTileState.setTileEntity(te);
            if (te instanceof Tickable) {
                tickables.add((Tickable) te);
            }
        }
        notifyListeners(newTileState, old);
        //TODO neighbour change notifications
        //        if (tileWorld.inBounds(tx + 1, ty)) {
        //            getTileStateGlobal(tx + 1, ty).getTile().neighbourChanged(tileWorld, newTileState);
        //        }
        //        if (tileWorld.inBounds(tx - 1, ty)) {
        //            getTileStateGlobal(tx - 1, ty).getTile().neighbourChanged(tileWorld, newTileState);
        //        }
        //        if (tileWorld.inBounds(tx, ty + 1)) {
        //            getTileStateGlobal(tx, ty + 1).getTile().neighbourChanged(tileWorld, newTileState);
        //        }
        //        if (tileWorld.inBounds(tx, ty - 1)) {
        //            getTileStateGlobal(tx, ty - 1).getTile().neighbourChanged(tileWorld, newTileState);
        //        }
        return old.getTile();
    }
    
    public Tile getBackground(int tx, int ty) {
        return this.tilesBackground.get(tx, ty).getTile();
    }
    
    public void setTileBackground(Tile t, int tx, int ty) {
        this.tilesBackground.set(new TileState(t, tx, ty), tx, ty);
    }
    
    public boolean inBounds(int gtx, int gty) {
        return gtx >= this.tx && gtx < this.tx + CHUNK_TILE_SIZE && gty >= this.ty && gty < this.ty + CHUNK_TILE_SIZE
                && worldAccessor.getWorldBounds().inBounds(gtx, gty);
    }
    
    public void tick(float time) {
        this.ticking = true;
        this.tickables.forEach((t) -> t.tick(time));
        this.ticking = false;
        while (!tickablesForRemoval.isEmpty()) {
            tickables.remove(tickablesForRemoval.poll());
        }
    }
    
    public void addEntity(Entity e) {
        if (ChunkMarkerCompMapper.has(e)) {
            ChunkMarkerComponent mw = ChunkMarkerCompMapper.get(e);
            if (mw.currentChunk != null) {
                throw new IllegalStateException();
            }
            mw.currentChunk = this;
        }
        this.entities.add(e);
    }
    
    public void removeEntity(Entity e) {
        if (ChunkMarkerCompMapper.has(e)) {
            ChunkMarkerComponent mw = ChunkMarkerCompMapper.get(e);
            if (mw.currentChunk == null) {
                throw new IllegalStateException();
            }
            mw.currentChunk = null;
        }
        this.entities.remove(e);
    }
    
    public List<Entity> getEntities() {
        return this.immutableEntities;
    }
    
    @Override
    public String toString() {
        return String.format("Chunk[x=%d, y=%d]", this.tx, this.ty);
    }
    
}
