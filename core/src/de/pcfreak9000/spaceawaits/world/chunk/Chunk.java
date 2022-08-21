package de.pcfreak9000.spaceawaits.world.chunk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.OrderedSet;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTList;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.nbt.NBTType;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.serialize.EntitySerializer;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;
import de.pcfreak9000.spaceawaits.util.Bounds;
import de.pcfreak9000.spaceawaits.world.NextTickTile;
import de.pcfreak9000.spaceawaits.world.RenderLayers;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkMarkerComponent;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.TickComponent;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.TickCounterSystem;
import de.pcfreak9000.spaceawaits.world.gen.IChunkGenerator;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.tile.ITileEntity;
import de.pcfreak9000.spaceawaits.world.tile.Tickable;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class Chunk implements NBTSerializable, Tickable, ITileArea {
    
    public static enum ChunkGenStage {
        Empty, Generated, Populated;
    }
    
    public static final int CHUNK_SIZE = 64;
    
    public static int toGlobalChunk(int globalTile) {
        return globalTile / CHUNK_SIZE; //<- This brings problems with negative numbers, but we dont use negative tile coordinates anyways
    }
    
    public static int toGlobalChunkf(float x) {
        return Mathf.floori(x / CHUNK_SIZE); //well i hope this floor function works properly
    }
    
    private final int rx;
    private final int ry;
    
    private final int tx;
    private final int ty;
    
    private final World world;
    
    private final TileStorage tiles;
    private final TileStorage tilesBackground;
    private final List<Entity> entities;
    private final List<Entity> immutableEntities;
    
    private final RenderTileStorage tilesRender;
    private final RenderTileStorage tilesBckRender;
    
    private final List<ChunkChangeListener> listeners;
    
    private OrderedSet<NextTickTile> tickTiles = new OrderedSet<>();
    
    private Bounds chunkBounds;
    
    private Engine addedToEngine;
    private TileSystem tileSystem;
    
    private ChunkGenStage genStage = ChunkGenStage.Empty;
    
    private final Entity chunkEntity;
    
    public Chunk(int rx, int ry, World world) {
        this.rx = rx;
        this.ry = ry;
        this.world = world;
        this.tx = rx * CHUNK_SIZE;
        this.ty = ry * CHUNK_SIZE;
        findAndSetActualBounds();
        this.listeners = new ArrayList<>();
        this.tiles = new TileStorage(world, CHUNK_SIZE, this.tx, this.ty, TileLayer.Front);
        this.tilesBackground = new TileStorage(world, CHUNK_SIZE, this.tx, this.ty, TileLayer.Back);
        
        this.entities = new ArrayList<>();
        this.immutableEntities = Collections.unmodifiableList(this.entities);
        this.chunkEntity = new EntityImproved();
        this.chunkEntity.flags = 1;
        
        this.chunkEntity.add(new TickComponent(this));
        
        PhysicsComponent pc = new PhysicsComponent();
        pc.factory = new ChunkPhysics(this);
        this.chunkEntity.add(pc);
        this.tilesRender = new RenderTileStorage(RenderLayers.TILE_FRONT, this, TileLayer.Front);
        this.tilesBckRender = new RenderTileStorage(RenderLayers.TILE_BACK, this, TileLayer.Back);
    }
    
    private void findAndSetActualBounds() {
        Bounds naive = new Bounds(tx, ty, CHUNK_SIZE, CHUNK_SIZE);
        this.chunkBounds = Bounds.intersect(naive, world.getBounds());
    }
    
    private void notifyListeners(TileState state, Tile newTile, Tile oldTile, int gtx, int gty, TileLayer layer) {
        for (ChunkChangeListener l : this.listeners) {
            l.onTileStateChange(this, state, newTile, oldTile, gtx, gty, layer);
        }
    }
    
    public void addListener(ChunkChangeListener listener) {
        this.listeners.add(listener);
    }
    
    public void generate(IChunkGenerator chunkGen) {
        if (genStage != ChunkGenStage.Empty) {
            throw new IllegalStateException();
        }
        genStage = ChunkGenStage.Generated;
        chunkGen.generateChunk(this);
    }
    
    public void populate(IChunkGenerator chunkGen) {
        if (genStage != ChunkGenStage.Generated) {
            throw new IllegalStateException();
        }
        genStage = ChunkGenStage.Populated;
        chunkGen.populateChunk(this, this.world);
    }
    
    public ChunkGenStage getGenStage() {
        return genStage;
    }
    
    public Bounds getBounds() {
        return this.chunkBounds;
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
    
    public boolean isActive() {
        return addedToEngine != null;
    }
    
    Engine getECS() {
        return addedToEngine;
    }
    
    public void addToECS(Engine ecs) {
        if (addedToEngine != null) {
            throw new IllegalStateException();
        }
        addedToEngine = ecs;
        this.tileSystem = ecs.getSystem(TileSystem.class);
        ecs.addEntity(chunkEntity);
        for (Entity e : entities) {
            ecs.addEntity(e);
        }
    }
    
    public void removeFromECS() {
        if (addedToEngine == null) {
            throw new IllegalStateException();
        }
        this.tileSystem = null;
        addedToEngine.removeEntity(chunkEntity);
        for (Entity e : entities) {
            addedToEngine.removeEntity(e);
        }
        addedToEngine = null;
    }
    
    private TileStorage getStorageForLayer(TileLayer layer) {
        switch (layer) {
        case Back:
            return tilesBackground;
        case Front:
            return tiles;
        default:
            throw new IllegalArgumentException(Objects.toString(layer));
        }
    }
    
    private RenderTileStorage getRenderStorageForLayer(TileLayer layer) {
        switch (layer) {
        case Back:
            return tilesBckRender;
        case Front:
            return tilesRender;
        default:
            throw new IllegalArgumentException(Objects.toString(layer));
        }
    }
    
    @Override
    public ITileEntity getTileEntity(int tx, int ty, TileLayer layer) {
        return this.getStorageForLayer(layer).get(tx, ty).getTileEntity();
    }
    
    @Override
    public Tile getTile(int tx, int ty, TileLayer layer) {
        return this.getStorageForLayer(layer).get(tx, ty).getTile();
    }
    
    //Not the best solution, but TileState visibility is a problem anyways
    TileState getTileState(int tx, int ty) {
        return this.tiles.get(tx, ty);
    }
    
    TileState getTileStateSafe(int tx, int ty) {
        return inBounds(tx, ty) ? getTileState(tx, ty) : null;
    }
    
    //Maybe save the set for later somehow?
    
    @Override
    public Tile setTile(int tx, int ty, TileLayer layer, Tile t) {
        Objects.requireNonNull(t);
        GameRegistry.TILE_REGISTRY.checkRegistered(t);
        TileStorage storage = getStorageForLayer(layer);
        RenderTileStorage renderStorage = getRenderStorageForLayer(layer);
        
        TileState state = storage.get(tx, ty);
        Tile oldTile = state.getTile();
        
        storage.set(t, tx, ty);
        
        renderStorage.removeTilePos(oldTile.getRendererMarkerComp(), tx, ty);
        renderStorage.addTilePos(t.getRendererMarkerComp(), tx, ty);
        
        notifyListeners(state, t, oldTile, tx, ty, layer);
        return oldTile;
    }
    
    @Override
    public boolean inBounds(int gtx, int gty) {
        return this.chunkBounds.inBounds(gtx, gty);
    }
    
    @Override
    public void tick(float time, long tick) {
        this.tiles.tick(time, tick);
        this.tilesBackground.tick(time, tick);
        this.tickTiles(tick);
    }
    
    private void tickTiles(long ticks) {
        Iterator<NextTickTile> it = tickTiles.iterator();
        while (it.hasNext()) {
            NextTickTile k = it.next();
            if (ticks >= k.getTick()) {
                it.remove();
                Tile t = getTile(k.getX(), k.getY(), k.getLayer());
                if (t == k.getTile()) {
                    t.updateTick(k.getX(), k.getY(), k.getLayer(), this.world, this.tileSystem, ticks);
                }
            }
        }
    }
    
    public void scheduleTick(int tx, int ty, TileLayer layer, Tile tile, int waitticks) {
        if (inBounds(tx, ty) && tile != null && tile != Tile.NOTHING) {
            tickTiles.add(new NextTickTile(tx, ty, layer, tile,
                    waitticks + world.getSystem(TickCounterSystem.class).getTick()));
        }
    }
    
    public void addEntity(Entity e) {
        if (Components.CHUNK_MARKER.has(e)) {
            ChunkMarkerComponent mw = Components.CHUNK_MARKER.get(e);
            if (mw.currentChunk != null) {
                throw new IllegalStateException();
            }
            mw.currentChunk = this;
        }
        this.entities.add(e);
    }
    
    public void removeEntity(Entity e) {
        if (Components.CHUNK_MARKER.has(e)) {
            ChunkMarkerComponent mw = Components.CHUNK_MARKER.get(e);
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
    
    @Override
    public void readNBT(NBTTag tag) {
        NBTCompound nbtc = (NBTCompound) tag;
        this.genStage = ChunkGenStage.values()[nbtc.getIntOrDefault("genStageIndex", 0)];
        NBTList entities = nbtc.getList("entities");
        NBTList tileList = nbtc.getList("tiles");
        NBTList tileEntities = nbtc.getList("tileEntities");
        NBTList ticklist = nbtc.getList("tileTicks");
        int cx = getGlobalTileX();
        int cy = getGlobalTileY();
        for (int i = 0; i < tileList.size(); i += 2) {
            int x = (i / 2) / CHUNK_SIZE;
            int y = (i / 2) % CHUNK_SIZE;
            //Foreground tiles
            String id = tileList.getString(i);
            Tile t = GameRegistry.TILE_REGISTRY.getOrDefault(id, Tile.NOTHING);
            setTile(cx + x, cy + y, TileLayer.Front, t);
            //Background tiles
            String idB = tileList.getString(i + 1);
            Tile tB = GameRegistry.TILE_REGISTRY.getOrDefault(idB, Tile.NOTHING);
            setTile(cx + x, cy + y, TileLayer.Back, tB);
        }
        for (NBTTag tet : tileEntities.getContent()) {
            NBTCompound comp = (NBTCompound) tet;
            byte x = comp.getByte("x");
            byte y = comp.getByte("y");
            TileState state = this.tiles.get(cx + x, cy + y);
            if (state.getTile().hasTileEntity()) {//Possibly check if the tileentitytype matches, in the future the default tile could change etc...
                if (state.getTileEntity() instanceof NBTSerializable) {
                    NBTSerializable seri = (NBTSerializable) state.getTileEntity();
                    NBTTag tedata = comp.get("data");
                    seri.readNBT(tedata);
                } //FIXME tileentities on the back layer arent saved/loaded
            }
        }
        for (NBTTag tet : ticklist.getContent()) {
            NextTickTile ntt = new NextTickTile(0, 0, null, null, -1);
            ntt.readNBT(tet);
            this.tickTiles.add(ntt);
        }
        if (entities.getEntryType() != NBTType.Compound) {
            throw new IllegalArgumentException("Entity list is not a compound list");
        }
        for (NBTTag t : entities.getContent()) {
            Entity e = EntitySerializer.deserializeEntity((NBTCompound) t);
            if (e != null) {
                addEntity(e);
            }
        }
    }
    
    @Override
    public NBTTag writeNBT() {
        NBTCompound chunkMaster = new NBTCompound();
        chunkMaster.putInt("genStageIndex", this.genStage.ordinal());
        NBTList tileList = new NBTList(NBTType.String);
        NBTList entities = new NBTList(NBTType.Compound);
        NBTList tileEntities = new NBTList(NBTType.Compound);
        NBTList tileMeta = new NBTList(NBTType.Compound);
        NBTList tileticks = new NBTList(NBTType.Compound);
        int cx = getGlobalTileX();
        int cy = getGlobalTileY();
        for (int i = 0; i < CHUNK_SIZE; i++) {
            for (int j = 0; j < CHUNK_SIZE; j++) {
                TileState st = this.tiles.get(cx + i, cy + j);
                String id = GameRegistry.TILE_REGISTRY.getId(st.getTile());
                tileList.addString(id);
                Tile t = st.getTile();
                if (t.hasTileEntity()) {
                    ITileEntity e = st.getTileEntity();
                    if (e instanceof NBTSerializable) {
                        NBTSerializable seri = (NBTSerializable) e;
                        NBTTag tag = seri.writeNBT();
                        NBTCompound einfo = new NBTCompound();
                        einfo.putByte("x", (byte) i);
                        einfo.putByte("y", (byte) j);
                        einfo.put("data", tag);
                        tileEntities.add(einfo);
                    }
                }
                TileState bst = this.tilesBackground.get(cx + i, cy + j);
                String bid = GameRegistry.TILE_REGISTRY.getId(bst.getTile());
                tileList.addString(bid);
            }
        }
        for (NextTickTile ntt : tickTiles) {
            tileticks.add(ntt.writeNBT());
        }
        chunkMaster.putList("tiles", tileList);
        chunkMaster.putList("tileEntities", tileEntities);
        chunkMaster.putList("tileMeta", tileMeta);
        chunkMaster.putList("tileTicks", tileticks);
        for (Entity e : this.entities) {
            if (EntitySerializer.isSerializable(e)) {
                NBTCompound nbt = EntitySerializer.serializeEntity(e);
                entities.add(nbt);
            }
        }
        chunkMaster.putList("entities", entities);
        return chunkMaster;
    }
    
}
