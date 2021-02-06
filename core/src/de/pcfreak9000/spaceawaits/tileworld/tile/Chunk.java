package de.pcfreak9000.spaceawaits.tileworld.tile;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Predicate;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.tileworld.WorldAccessor;
import de.pcfreak9000.spaceawaits.tileworld.ecs.chunk.ChunkComponent;
import de.pcfreak9000.spaceawaits.tileworld.ecs.chunk.ChunkRenderComponent;

public class Chunk {
    
    public static final int CHUNK_TILE_SIZE = 64;
    
    public static int toGlobalChunk(int globalTile) {
        return (int) Math.floor(globalTile / (double) CHUNK_TILE_SIZE);//TODO use other floor
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
    
    private final Queue<Tickable> tickablesForRemoval;
    private boolean ticking = false;
    
    private final Entity regionEntity;
    
    public Chunk(int rx, int ry, WorldAccessor worldAccessor) {
        this.rx = rx;
        this.ry = ry;
        this.tx = rx * CHUNK_TILE_SIZE;
        this.ty = ry * CHUNK_TILE_SIZE;
        this.tiles = new TileStorage(CHUNK_TILE_SIZE, this.tx, this.ty);
        this.tilesBackground = new TileStorage(CHUNK_TILE_SIZE, this.tx, this.ty);
        this.tileEntities = new ArrayList<>();
        this.tickables = new ArrayList<>();
        this.entities = new ArrayList<>();
        this.tickablesForRemoval = new ArrayDeque<>();
        this.regionEntity = new Entity();
        this.regionEntity.add(new ChunkComponent(this));
        this.regionEntity.add(new ChunkRenderComponent());//TMP because server side stuff
        this.worldAccessor = worldAccessor;
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
    
    //    private TileState getTileStateGlobal(int tx, int ty) {
    //        int rx = Chunk.toGlobalChunk(tx);
    //        int ry = Chunk.toGlobalChunk(ty);
    //        Chunk r = tileWorld.requestRegion(rx, ry);
    //        return r.getTileState(tx, ty);
    //    }
    //    
    //    private TileState getTileState(int x, int y) {
    //        return this.tiles.get(x, y);
    //    }
    
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
                && worldAccessor.getMeta().inBounds(gtx, gty);
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
        this.entities.add(e);
    }
    
    public void removeEntity(Entity e) {
        this.entities.remove(e);
    }
    
    public List<Entity> getEntities(){
        return this.entities;
    }
    
    @Override
    public String toString() {
        return String.format("Region[x=%d, y=%d]", this.tx, this.ty);
    }
}
