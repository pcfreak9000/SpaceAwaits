package de.pcfreak9000.spaceawaits.tileworld.tile;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Predicate;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.tileworld.ecs.RegionComponent;

public class Region {
    
    private static final Logger LOGGER = Logger.getLogger(Region.class);
    
    private static final boolean DEBUG_SHOW_BORDERS = false;
    
    public static final int REGION_TILE_SIZE = 64;
    
    private static final float BACKGROUND_FACTOR = 0.5f;
    
    public static int toGlobalRegion(int globalTile) {
        return (int) Math.floor(globalTile / (double) REGION_TILE_SIZE);//TODO use other floor
    }
    
    private final int rx;
    private final int ry;
    
    private final int tx;
    private final int ty;
    
    private final TileWorld tileWorld;
    
    private final TileStorage tiles;
    private final TileStorage tilesBackground;
    private final List<TileEntity> tileEntities;
    private final List<Tickable> tickables;
    
    private final Queue<Tickable> tickablesForRemoval;
    private boolean ticking = false;
    
    private final Entity regionEntity;
    
    private boolean recacheTiles;
    public int cacheId = -1;
    private int backgroundLength = 0;
    private int length = 0;
    
    public Region(int rx, int ry, TileWorld tw) {
        this.tileWorld = tw;
        this.rx = rx;
        this.ry = ry;
        this.tx = rx * REGION_TILE_SIZE;
        this.ty = ry * REGION_TILE_SIZE;
        this.tiles = new TileStorage(REGION_TILE_SIZE, this.tx, this.ty);
        this.tilesBackground = new TileStorage(REGION_TILE_SIZE, this.tx, this.ty);
        this.tileEntities = new ArrayList<>();
        this.tickables = new ArrayList<>();
        this.tickablesForRemoval = new ArrayDeque<>();
        this.regionEntity = new Entity();
        this.regionEntity.add(new RegionComponent(this));
    }
    //HMm
    public void queueRecacheTiles() {
        this.recacheTiles = true;
    }
    
    public int getGlobalRegionX() {
        return this.rx;
    }
    
    public int getGlobalRegionY() {
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
    
    public Tile getTile(int tx, int ty) {
        return this.tiles.get(tx, ty).getTile();
    }
    
    private TileState getTileStateGlobal(int tx, int ty) {
        int rx = Region.toGlobalRegion(tx);
        int ry = Region.toGlobalRegion(ty);
        Region r = tileWorld.requestRegion(rx, ry);
        return r.getTileState(tx, ty);
    }
    
    private TileState getTileState(int x, int y) {
        return this.tiles.get(x, y);
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
                if (ticking) {
                    tickablesForRemoval.add((Tickable) old.getTileEntity());
                } else {
                    tickables.remove(old.getTileEntity());
                }
            }
            old.setTileEntity(null);
        }
        if (t.hasTileEntity()) {
            TileEntity te = t.createTileEntity(tileWorld, newTileState);
            this.tileEntities.add(te);
            newTileState.setTileEntity(te);
            if (te instanceof Tickable) {
                tickables.add((Tickable) te);
            }
        }
        
        //newTileState.sunlight().set(old.sunlight());
        //newTileState.setDirectSun(old.isDirectSun());
        //requestSunlightComputation();
        
        queueRecacheTiles();
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
        return gtx >= this.tx && gtx < this.tx + REGION_TILE_SIZE && gty >= this.ty && gty < this.ty + REGION_TILE_SIZE
                && gtx < tileWorld.getWorldWidth() && gty < tileWorld.getWorldHeight();
    }
    
    public void tick(float time) {
        this.ticking = true;
        this.tickables.forEach((t) -> t.tick(time));
        this.ticking = false;
        while (!tickablesForRemoval.isEmpty()) {
            tickables.remove(tickablesForRemoval.poll());
        }
    }
    

    //TMP
    public int len() {
        return length;
    }
    
    //TMP?!
    public boolean recacheTiles() {
        return recacheTiles;
    }
    
    //TMP?!
    public void recacheTiles(SpriteBatch cache) {
        //LOGGER.debug("Recaching: " + toString());
        //cache.beginCache(cacheId);
        List<TileState> tiles = new ArrayList<>();
        Predicate<TileState> predicate = (t) -> t.getTile().color().a > 0;//Maybe just iterate the whole tilestorage or something, first collecting everything is probably slow
        //background does not need to be recached all the time because it can not change (rn)?
//        this.tilesBackground.getAll(tiles, predicate);
//        Color backgroundColor = new Color();
//        this.backgroundLength = 0;
        this.length = 0;
//        for (TileState t : tiles) {
//            backgroundColor.set(t.getTile().color());
//            backgroundColor.mul(BACKGROUND_FACTOR, BACKGROUND_FACTOR, BACKGROUND_FACTOR, 1);
//            cache.setColor(backgroundColor);
//            addTile(t, cache);
//            this.backgroundLength++;
//            this.length++;
//        }
//        tiles.clear();
        this.tiles.getAll(tiles, predicate);
        for (TileState t : tiles) {
            cache.setColor(t.getTile().color());
            addTile(t, cache);
            this.length++;
        }
        //recacheTiles = false;
        //cache.endCache();
    }
    
    private void addTile(TileState t, SpriteBatch c) {//TODO tile texture and animations and stuff
        c.draw(t.getTile().getTextureRegion(), t.getGlobalTileX() * Tile.TILE_SIZE, t.getGlobalTileY() * Tile.TILE_SIZE, Tile.TILE_SIZE,
                Tile.TILE_SIZE);
    }
    
    @Override
    public String toString() {
        return String.format("Region[x=%d, y=%d]", this.tx, this.ty);
    }
}
