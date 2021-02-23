package de.pcfreak9000.spaceawaits.world.tile;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Predicate;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.world.WorldAccessor;
import de.pcfreak9000.spaceawaits.world.ecs.PhysicsSystemBox2D;
import de.pcfreak9000.spaceawaits.world.ecs.chunk.ChunkComponent;
import de.pcfreak9000.spaceawaits.world.ecs.chunk.ChunkRenderComponent;

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
    
    private final List<ChunkChangeListener> listeners;
    
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
        this.listeners = new ArrayList<>();
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
        adjustFixtures(newTileState, old);
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
    
    private Body body;
    
    private void adjustFixtures(TileState newstate, TileState oldstate) {
        if (body == null) {
            if (newstate.getFixture() != null) {
                body = newstate.getFixture().getBody();
            } else if (oldstate.getFixture() != null) {
                body = oldstate.getFixture().getBody();
            }
        }
        if (oldstate.getTile().isSolid() != newstate.getTile().isSolid()) {
            if (!newstate.getTile().isSolid() && body != null) { // oldstate was solid
                if (oldstate.getFixture() != null) {
                    body.destroyFixture(oldstate.getFixture());
                    oldstate.setFixture(null);
                }
                int x = newstate.getGlobalTileX();
                int y = newstate.getGlobalTileY();
                int topy = y + 1;
                int boty = y - 1;
                int rightx = x + 1;
                int leftx = x - 1;
                TileState top = inBounds(x, topy) ? getTileState(x, topy) : null;
                TileState bot = inBounds(x, boty) ? getTileState(x, boty) : null;
                TileState right = inBounds(rightx, y) ? getTileState(rightx, y) : null;
                TileState left = inBounds(leftx, y) ? getTileState(leftx, y) : null;
                if (top != null && top.getTile().isSolid() && top.getFixture() == null) {
                    createFixture(x, topy, body);
                }
                if (bot != null && bot.getTile().isSolid() && bot.getFixture() == null) {
                    createFixture(x, boty, body);
                }
                if (right != null && right.getTile().isSolid() && right.getFixture() == null) {
                    createFixture(rightx, y, body);
                }
                if (left != null && left.getTile().isSolid() && left.getFixture() == null) {
                    createFixture(leftx, y, body);
                }
            } else { //newstate is solid, oldstate wasn't
                //TODO
            }
        }
    }
    
    private void createFixture(int gtx, int gty, Body chunkbody) {
        PolygonShape shape = new PolygonShape();
        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        shape.setAsBox(PhysicsSystemBox2D.METER_CONV.in(Tile.TILE_SIZE / 2),
                PhysicsSystemBox2D.METER_CONV.in(Tile.TILE_SIZE / 2),
                new Vector2(PhysicsSystemBox2D.METER_CONV.in((gtx - this.tx) * Tile.TILE_SIZE),
                        PhysicsSystemBox2D.METER_CONV.in((gty - this.ty) * Tile.TILE_SIZE)),
                0);
        Fixture fix = chunkbody.createFixture(fd);
        getTileState(gtx, gty).setFixture(fix);
        shape.dispose();
        //fix.setUserData(t); //TODO
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
    
    public List<Entity> getEntities() {
        return this.entities;
    }
    
    @Override
    public String toString() {
        return String.format("Region[x=%d, y=%d]", this.tx, this.ty);
    }
    
}
