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
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTList;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.nbt.NBTType;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.serialize.EntitySerializer;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;
import de.pcfreak9000.spaceawaits.world.WorldAccessor;
import de.pcfreak9000.spaceawaits.world.ecs.chunk.ChunkComponent;
import de.pcfreak9000.spaceawaits.world.ecs.chunk.ChunkRenderComponent;
import de.pcfreak9000.spaceawaits.world.ecs.entity.ChunkMarkerComponent;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;

public class Chunk implements NBTSerializable {
    
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
    
    @Override
    public void readNBT(NBTTag tag) {
        NBTCompound nbtc = (NBTCompound) tag;
        NBTList entities = nbtc.getList("entities");
        NBTList tileList = nbtc.getList("vgr");
        NBTList tileBkgrList = nbtc.getList("bkr");
        NBTList tileEntities = nbtc.getList("tileEntities");
        for (int i = 0; i < tileList.size(); i++) {
            String id = tileList.getString(i);
            Tile t = GameRegistry.TILE_REGISTRY.getOrDefault(id, Tile.EMPTY);
            int x = i / CHUNK_TILE_SIZE;
            int y = i % CHUNK_TILE_SIZE;
            setTile(t, getGlobalTileX() + x, getGlobalTileY() + y);
            i++;
        }
        for (int i = 0; i < tileBkgrList.size(); i++) {
            String id = tileBkgrList.getString(i);
            Tile t = GameRegistry.TILE_REGISTRY.getOrDefault(id, Tile.EMPTY);
            int x = i / CHUNK_TILE_SIZE;
            int y = i % CHUNK_TILE_SIZE;
            setTileBackground(t, getGlobalTileX() + x, getGlobalTileY() + y);
            i++;
        }
        for (NBTTag tet : tileEntities.getContent()) {
            NBTCompound comp = (NBTCompound) tet;
            int x = comp.getInt("x");
            int y = comp.getInt("y");
            TileState state = this.tiles.get(x, y);
            if (state.getTile().hasTileEntity()) {//Possibly check if the tileentitytype matches, in the future the default tile could change etc...
                if (state.getTileEntity() instanceof NBTSerializable) {
                    NBTSerializable seri = (NBTSerializable) state.getTileEntity();
                    NBTTag tedata = comp.get("data");
                    seri.readNBT(tedata);
                }
            }
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
        NBTList tileList = new NBTList(NBTType.String);
        NBTList tileBkgrList = new NBTList(NBTType.String);//TileLists can probably be converted into one
        NBTList entities = new NBTList(NBTType.Compound);
        NBTList tileEntities = new NBTList(NBTType.Compound);
        for (int i = 0; i < CHUNK_TILE_SIZE; i++) {
            for (int j = 0; j < CHUNK_TILE_SIZE; j++) {
                TileState st = this.tiles.get(this.getGlobalTileX() + i, this.getGlobalTileY() + j);
                String id = GameRegistry.TILE_REGISTRY.getId(st.getTile());
                tileList.addString(id);
                Tile t = st.getTile();
                if (t.hasTileEntity()) {
                    TileEntity e = st.getTileEntity();
                    if (e instanceof NBTSerializable) {
                        NBTSerializable seri = (NBTSerializable) e;
                        NBTTag tag = seri.writeNBT();
                        NBTCompound einfo = new NBTCompound();
                        einfo.putInt("x", st.getGlobalTileX());
                        einfo.putInt("y", st.getGlobalTileY());//Could become bytes in the future?
                        einfo.put("data", tag);
                        tileEntities.add(einfo);
                    }
                }
                
                TileState bst = this.tilesBackground.get(this.getGlobalTileX() + i, this.getGlobalTileY() + j);
                String bid = GameRegistry.TILE_REGISTRY.getId(bst.getTile());
                tileBkgrList.addString(bid);
            }
        }
        chunkMaster.putList("vgr", tileList);
        chunkMaster.putList("bkr", tileBkgrList);
        chunkMaster.putList("tileEntities", tileEntities);
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
