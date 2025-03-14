package de.pcfreak9000.spaceawaits.world.tile.ecs;

import java.util.Iterator;
import java.util.Objects;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongMap;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.core.ecs.SystemCache;
import de.pcfreak9000.spaceawaits.core.ecs.content.RandomSystem;
import de.pcfreak9000.spaceawaits.core.ecs.content.TickCounterSystem;
import de.pcfreak9000.spaceawaits.core.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.util.Direction;
import de.pcfreak9000.spaceawaits.util.IntCoords;
import de.pcfreak9000.spaceawaits.world.ITileArea;
import de.pcfreak9000.spaceawaits.world.breaking.IBreaker;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkSystem;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.ecs.OnNeighbourChangeComponent;
import de.pcfreak9000.spaceawaits.world.ecs.WorldSystem;
import de.pcfreak9000.spaceawaits.world.physics.IRaycastTileCallback;
import de.pcfreak9000.spaceawaits.world.physics.UserDataHelper;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsSystem;
import de.pcfreak9000.spaceawaits.world.render.RenderLayers;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.tile.BreakTileProgress;
import de.pcfreak9000.spaceawaits.world.tile.ITileEntity;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class TileSystem extends EntitySystem implements ITileArea {
    
    private final LongMap<BreakTileProgress> breakingTiles = new LongMap<>();
    private final Entity entity;
    
    private SystemCache<PhysicsSystem> phys = new SystemCache<>(PhysicsSystem.class);
    private SystemCache<ChunkSystem> chunks = new SystemCache<>(ChunkSystem.class);
    private SystemCache<WorldSystem> worldsys = new SystemCache<>(WorldSystem.class);
    private SystemCache<TickCounterSystem> tcsys = new SystemCache<>(TickCounterSystem.class);
    private SystemCache<RandomSystem> randsys = new SystemCache<>(RandomSystem.class);
    
    private UserDataHelper ud = new UserDataHelper();
    
    public TileSystem() {
        this.entity = createInfoEntity();
    }
    
    private Entity createInfoEntity() {
        Entity e = new EntityImproved();
        e.add(new BreakingTilesComponent(this.breakingTiles));
        e.add(new RenderComponent(RenderLayers.TILE_EFFECT));
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
    @Override
    public Tile setTile(int tx, int ty, TileLayer layer, Tile tile) {
        Chunk c = getChunkForTile(tx, ty);
        if (c != null) {
            Tile old = c.setTile(tx, ty, layer, tile);
            breakingTiles.remove(IntCoords.toLong(tx, ty));
            old.onTileRemoved(tx, ty, layer, getEngine(), this);
            tile.onTileSet(tx, ty, layer, getEngine(), this);//Hmmmmm, oof. Decide if this listener stuff happens in chunk or here
            notifyNeighbours(tile, old, tx, ty, layer);
            return old;
        }
        return null;
    }
    
    @Override
    public Tile removeTile(int tx, int ty, TileLayer layer) {
        return setTile(tx, ty, layer, worldsys.get(getEngine()).getWorldProperties().getTileDefault(tx, ty, layer));
    }
    
    //Hmmm. What about tiles on the edge to only loaded but not updated? What about resonance cascades?
    private void notifyNeighbours(Tile tile, Tile old, int tx, int ty, TileLayer layer) {
        for (Direction d : Direction.VONNEUMANN_NEIGHBOURS) {
            int i = tx + d.dx;
            int j = ty + d.dy;
            getTile(i, j, layer).onNeighbourChange(getEngine(), this, i, j, tile, old, tx, ty, layer,
                    randsys.get(getEngine()).getRandom());
        }
        getTile(tx, ty, layer.other()).onNeighbourChange(getEngine(), this, tx, ty, tile, old, tx, ty, layer.other(),
                randsys.get(getEngine()).getRandom());
        phys.get(getEngine()).queryAABB(tx - 0.1f, ty - 0.1f, tx + 1 + 0.1f * 2, ty + 1 + 0.1f * 2, (fix, conv) -> {
            ud.set(fix.getUserData(), fix);
            if (ud.isEntity()) {//This might trigger multiple times for one entity that has more than one fixture!
                Entity e = ud.getEntity();
                if (Components.NEIGHGOUR_CHANGED.has(e)) {
                    OnNeighbourChangeComponent oncc = Components.NEIGHGOUR_CHANGED.get(e);
                    if (oncc.validate(e)) {
                        oncc.onNeighbourTileChange.onNeighbourTileChange(getEngine(), this, e, tile, old, tx, ty,
                                layer);
                    }
                }
            }
            return true;
        });
    }
    
    @Override
    public Tile getTile(int tx, int ty, TileLayer layer) {
        Chunk c = getChunkForTile(tx, ty);
        if (c != null) {
            return c.getTile(tx, ty, layer);
        }
        return Tile.NOTHING;
    }
    
    private Chunk getChunkForTile(int tx, int ty) {
        return chunks.get(getEngine()).getChunk(Chunk.toGlobalChunk(tx), Chunk.toGlobalChunk(ty));
    }
    
    @Override
    public ITileEntity getTileEntity(int tx, int ty, TileLayer layer) {
        Chunk c = getChunkForTile(tx, ty);
        if (c != null) {
            return c.getTileEntity(tx, ty, layer);
        }
        return null;
    }
    
    public void scheduleTick(int tx, int ty, TileLayer layer, Tile tile, int waitticks) {
        Chunk c = getChunkForTile(tx, ty);
        if (c != null) {
            c.scheduleTick(tcsys.get(getEngine()), tx, ty, layer, tile, waitticks);
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
        if (current != null && (!current.canBeReplacedBy(tile) || current == tile)) {
            //check current occupation, only place tile if there isnt already one present
            return null;
        }
        Array<Entity> ents = new Array<>();
        Direction reloc = null;
        if (tile.isSolid() && layer == TileLayer.Front) {
            boolean[] blocking = new boolean[1];
            phys.get(getEngine()).queryAABB(tx + 0.15f, ty + 0.15f, tx + 0.7f, ty + 0.7f, (fix, conv) -> {
                ud.set(fix.getUserData(), fix);
                if (fix.isSensor()) {
                    if (!ud.isEntity() || !Components.PHYSICS.get(ud.getEntity()).considerSensorsAsBlocking) {
                        return true;
                    }
                }
                if (ud.isEntity()) {
                    if (Components.ITEM_STACK.has(ud.getEntity())) {//Maybe exchange for some MakesWayForTileComponent? Might get complicated for entities bigger than a Tile
                        ents.add(ud.getEntity());
                    } else {
                        blocking[0] = true;
                        return false;
                    }
                }
                return true;
            });
            if (blocking[0]) {
                return null;
            }
            if (!ents.isEmpty()) {
                for (Direction d : Direction.VONNEUMANN_NEIGHBOURS) {
                    if (!phys.get(getEngine()).checkRectOccupation(tx + d.dx + 0.15f, ty + d.dy + 0.15f, 0.7f, 0.7f,
                            true)) {//This doesn't account for possible item mergers...
                        reloc = d;
                        break;
                    }
                }
                if (reloc == null) {
                    return null;
                }
            }
        }
        if (!tile.canPlace(tx, ty, layer, getEngine(), this)) {
            return null;
        }
        for (Entity e : ents) {
            TransformComponent tc = Components.TRANSFORM.get(e);
            tc.position.add(reloc.dx, reloc.dy);
        }
        Tile ret = setTile(tx, ty, layer, tile);
        tile.onTilePlaced(tx, ty, layer, getEngine(), this);
        return ret;
    }
    
    public float breakTile(int tx, int ty, TileLayer layer, IBreaker breaker) {
        Objects.requireNonNull(breaker);
        //First check if this is allowed
        //Tile specific checks:
        if (layer == TileLayer.Back) {
            Tile front = getTile(tx, ty, TileLayer.Front);
            if (front.isSolid()) {
                return IBreaker.ABORTED_BREAKING;
            }
        }
        //******************
        //Check breakable/breaker compatibility:
        Tile tile = getTile(tx, ty, layer);
        if (tile == Tile.NOTHING) {
            return IBreaker.ABORTED_BREAKING;
        }
        if (!tile.canBreak()) {
            return IBreaker.ABORTED_BREAKING;
        }
        if (!breaker.canBreak(getEngine(), tile)) {
            return IBreaker.ABORTED_BREAKING;
        }
        //********************************
        //break stuff:
        long l = IntCoords.toLong(tx, ty);
        BreakTileProgress t = breakingTiles.get(l);
        if (t == null || t.getLayer() != layer) {
            t = new BreakTileProgress(tx, ty, layer);
            breakingTiles.put(l, t);
        }
        float speedActual = breaker.breakIt(getEngine(), tile, t.getProgress());
        t.incProgress(speedActual * GameScreen.STEPLENGTH_SECONDS);
        if (t.getProgress() >= IBreaker.FINISHED_BREAKING) {
            //********************************+
            //Handle tile breaking:
            Array<ItemStack> drops = new Array<>();
            tile.collectDrops(getEngine(), randsys.get(getEngine()).getRandom(), tx, ty, layer, drops);
            tile.onTileBreak(tx, ty, layer, getEngine(), this, breaker);
            breaker.onBreak(getEngine(), tile, drops, randsys.get(getEngine()).getRandom());
            removeTile(tx, ty, layer);
            if (drops.size > 0) {
                ItemStack.dropRandomInTile(drops, getEngine(), tx, ty, randsys.get(getEngine()).getRandom());
                drops.clear();
            }
            return IBreaker.FINISHED_BREAKING;
            //*************************
        }
        return MathUtils.clamp(t.getProgress(), 0, 1);
    }
    
    public void raycastTiles(float x1, float y1, float x2, float y2, TileLayer layer,
            IRaycastTileCallback tileCallback) {
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
    
    @Override
    public boolean inBounds(int tx, int ty) {
        return worldsys.get(getEngine()).getBounds().inBounds(tx, ty);
    }
    
}
