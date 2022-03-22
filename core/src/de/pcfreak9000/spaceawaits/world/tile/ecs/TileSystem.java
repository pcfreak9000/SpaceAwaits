package de.pcfreak9000.spaceawaits.world.tile.ecs;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongMap;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemEntityFactory;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.util.Direction;
import de.pcfreak9000.spaceawaits.util.IntCoords;
import de.pcfreak9000.spaceawaits.world.IChunkProvider;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.physics.IRaycastTileCallback;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsSystem;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.tile.BreakTileProgress;
import de.pcfreak9000.spaceawaits.world.tile.IMetadata;
import de.pcfreak9000.spaceawaits.world.tile.ITileBreaker;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.TileEntity;

public class TileSystem extends EntitySystem {
    
    private World world;
    private Random worldRandom;
    private IChunkProvider chunkProvider;
    private final LongMap<BreakTileProgress> breakingTiles = new LongMap<>();
    private final Entity entity;
    
    private PhysicsSystem physicsSystem;
    
    public TileSystem(World world, Random r, IChunkProvider ch) {
        this.world = world;
        this.worldRandom = r;
        this.chunkProvider = ch;
        this.entity = createInfoEntity();
    }
    
    private PhysicsSystem getPhysicsSystem() {
        if (this.physicsSystem == null) {//TODO tmp, make an entity with stuff like this maybe?
            this.physicsSystem = getEngine().getSystem(PhysicsSystem.class);
        }
        return this.physicsSystem;
    }
    
    private Entity createInfoEntity() {
        Entity e = new EntityImproved();
        e.add(new BreakingTilesComponent(this.breakingTiles));
        e.add(new RenderComponent(1, "break"));
        return e;
    }
    
    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntity(entity);
        
    }
    
    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        engine.removeEntity(entity);
        this.physicsSystem = null;
    }
    
    @Override
    public void update(float deltaTime) {
        Iterator<BreakTileProgress> it = breakingTiles.values().iterator();
        while (it.hasNext()) {
            BreakTileProgress t = it.next();
            if (t.getLast() == t.getProgress()) {
                it.remove();
            } else {
                t.setLast(t.getProgress());
            }
        }
    }
    
    /**
     * Sets a tile.
     * 
     * @param tx    tile x
     * @param ty    tile y
     * @param layer tilelayer
     * @param tile  the new Tile
     * @return the old tile or null if nothing changed (out of bounds or not loaded)
     */
    public Tile setTile(int tx, int ty, TileLayer layer, Tile tile) {
        Chunk c = getChunkForTile(tx, ty);
        if (c != null) {
            Tile old = c.setTile(tx, ty, layer, tile);
            breakingTiles.remove(IntCoords.toLong(tx, ty));
            old.onTileRemoved(tx, ty, layer, world, this);
            tile.onTileSet(tx, ty, layer, world, this);//Hmmmmm, oof. Decide if this listener stuff happens in chunk or here
            notifyNeighbours(tile, old, tx, ty, layer);
            return old;
        }
        return null;
    }
    
    public Tile removeTile(int tx, int ty, TileLayer layer) {
        return setTile(tx, ty, layer, this.world.getWorldProperties().getTileDefault(tx, ty, layer));
    }
    
    //Hmmm. What about tiles on the edge to only loaded but not updated? What about resonance cascades?
    private void notifyNeighbours(Tile tile, Tile old, int tx, int ty, TileLayer layer) {
        for (Direction d : Direction.VONNEUMANN_NEIGHBOURS) {
            int i = tx + d.dx;
            int j = ty + d.dy;
            getTile(i, j, layer).onNeighbourChange(world, this, i, j, tile, old, tx, ty, layer);
        }
    }
    
    public Tile getTile(int tx, int ty, TileLayer layer) {
        Chunk c = getChunkForTile(tx, ty);
        if (c != null) {
            return c.getTile(tx, ty, layer);
        }
        return Tile.NOTHING;
    }
    
    public IMetadata getMetadata(int tx, int ty, TileLayer layer) {
        Chunk c = getChunkForTile(tx, ty);
        if (c != null) {
            return c.getMetadata(tx, ty, layer);
        }
        return null;
    }
    
    private Chunk getChunkForTile(int tx, int ty) {
        if (world.getBounds().inBounds(tx, ty)) {
            Chunk c = chunkProvider.getChunk(Chunk.toGlobalChunk(tx), Chunk.toGlobalChunk(ty));
            return c;
        }
        return null;
    }
    
    public TileEntity getTileEntity(int tx, int ty, TileLayer layer) {
        Chunk c = getChunkForTile(tx, ty);
        if (c != null) {
            return c.getTileEntity(tx, ty, layer);
        }
        return null;
    }
    
    public void scheduleTick(int tx, int ty, TileLayer layer, Tile tile, int waitticks) {
        Chunk c = getChunkForTile(tx, ty);
        if (c != null) {
            c.scheduleTick(tx, ty, layer, tile, waitticks);
        }
        //do something if this fails?
    }
    
    public Tile placeTile(int tx, int ty, TileLayer layer, Tile tile) {
        if (layer == TileLayer.Back) {//if back layer, check for front layer? basically a gameplay decision, not sure
            Tile front = getTile(tx, ty, TileLayer.Front);
            if (front.isSolid() || front.isOpaque()) {
                return null;
            }
        }
        Tile current = getTile(tx, ty, layer);
        if (current != null && (!current.canBeReplaced() || current == tile)) {
            //check current occupation, only place tile if there isnt already one
            return null;
        }
        if (tile.isSolid() && layer == TileLayer.Front) {
            if (getPhysicsSystem().checkRectEntityOccupation(tx, ty, tx + 0.99f, ty + 0.99f)) {
                return null;
            }
        }
        if (!tile.canPlace(tx, ty, layer, world, this)) {
            return null;
        }
        Tile ret = setTile(tx, ty, layer, tile);
        tile.onTilePlaced(tx, ty, layer, world, this);
        return ret;
    }
    
    public float breakTile(int tx, int ty, TileLayer layer, ITileBreaker breaker) {
        //TODO allow null tilebreaker?
        //First check if this is allowed
        if (layer == TileLayer.Back) {
            Tile front = getTile(tx, ty, TileLayer.Front);
            if (front.isSolid()) {
                return -1f;
            }
        }
        Tile tile = getTile(tx, ty, layer);
        if (!tile.canBreak() && !breaker.ignoreTileCanBreak()) {
            return -1f;
        }
        if (tile.getMaterialLevel() > breaker.getMaterialLevel()
                && breaker.getMaterialLevel() != Float.POSITIVE_INFINITY) {
            return -1f;
        }
        if (!breaker.canBreak(tx, ty, layer, tile, world, this)) {
            return -1f;
        }
        long l = IntCoords.toLong(tx, ty);
        BreakTileProgress t = breakingTiles.get(l);
        if (t == null || t.getLayer() != layer) {
            t = new BreakTileProgress(tx, ty, layer);
            breakingTiles.put(l, t);
        }
        float speedActual = breaker.getSpeed() / tile.getHardness();
        t.incProgress(speedActual * World.STEPLENGTH_SECONDS);
        if (t.getProgress() >= 1f) {
            Array<ItemStack> drops = new Array<>();
            setTile(tx, ty, layer, world.getWorldProperties().getTileDefault(tx, ty, layer));
            tile.onTileBroken(tx, ty, layer, drops, world, this, worldRandom);
            breaker.onTileBreak(tx, ty, layer, tile, world, this, drops, worldRandom);
            if (drops.size > 0) {
                for (ItemStack s : drops) {
                    Entity e = ItemEntityFactory.setupItemEntity(s,
                            tx + worldRandom.nextFloat() / 2f - Item.WORLD_SIZE / 2,
                            ty + worldRandom.nextFloat() / 2F - Item.WORLD_SIZE / 2);
                    world.spawnEntity(e, false);
                }
                drops.clear();
            }
            return 1f;
        }
        return Mathf.clamp(t.getProgress(), 0, 1f);
    }
    
    public void raycastTiles(IRaycastTileCallback tileCallback, float x1, float y1, float x2, float y2,
            TileLayer layer) {
        /*
         * Based on the video "Super Fast Ray Casting in Tiled Worlds using DDA" by
         * javidx9 (2021, https://www.youtube.com/watch?v=NbSee-XM7WA).
         */
        //constants
        final int txStart = Tile.toGlobalTile(x1);
        final int tyStart = Tile.toGlobalTile(y1);
        final int txTarget = Tile.toGlobalTile(x2);
        final int tyTarget = Tile.toGlobalTile(y2);
        final float dx = x2 - x1;
        final float dy = y2 - y1;
        final float rayUnitStepSizeX = (float) Math.sqrt(1 + Mathf.square(dy / dx));
        final float rayUnitStepSizeY = (float) Math.sqrt(1 + Mathf.square(dx / dy));
        final int stepX = (int) Math.signum(dx);
        final int stepY = (int) Math.signum(dy);
        //prep loop vars
        float lenx, leny;
        if (dx < 0) {
            lenx = x1 - txStart;
        } else {
            lenx = txStart + 1 - x1;
        }
        if (dy < 0) {
            leny = y1 - tyStart;
        } else {
            leny = tyStart + 1 - y1;
        }
        lenx *= rayUnitStepSizeX;
        leny *= rayUnitStepSizeY;
        int tx = txStart;
        int ty = tyStart;
        for (int i = 0; i < Chunk.CHUNK_SIZE * 10; i++) {//while(true) is not so nice
            Tile t = getTile(tx, ty, layer);
            boolean continu = tileCallback.reportRayTile(t, tx, ty);
            if (!continu || (tx == txTarget && ty == tyTarget)) {
                break;
            }
            if (lenx < leny) {
                tx += stepX;
                lenx += rayUnitStepSizeX;
            } else {
                ty += stepY;
                leny += rayUnitStepSizeY;
            }
        }
    }
    
    public void queryAABBTiles(float x, float y, float w, float h, TileLayer layer, Array<Tile> res) {
        int xi = Mathf.floori(x);
        int yi = Mathf.floori(y);
        int xwi = Mathf.ceili(x + w);
        int yhi = Mathf.ceili(y + h);
        for (int i = xi; i < xwi; i++) {
            for (int j = yi; j < yhi; j++) {
                res.add(getTile(i, j, layer));
            }
        }
    }
    
    public boolean checkSolidOccupation(float x, float y, float w, float h) {
        if (getPhysicsSystem().checkRectEntityOccupation(x, y, x + w, y + h)) {
            return true;
        }
        int ix = Mathf.floori(x);
        int iy = Mathf.floori(y);
        int iw = Mathf.ceili(x + w);
        int ih = Mathf.ceili(y + h);
        for (int i = ix; i < iw; i++) {
            for (int j = iy; j < ih; j++) {
                Tile t = getTile(i, j, TileLayer.Front);
                if (t.isSolid()) {
                    return true;
                }
            }
        }
        return false;
    }
    
}
