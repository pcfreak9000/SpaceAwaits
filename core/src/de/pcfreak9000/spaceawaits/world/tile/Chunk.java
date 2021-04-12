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
import de.pcfreak9000.spaceawaits.world.render.RenderComponent;

public class Chunk implements NBTSerializable {

    private static final ComponentMapper<ChunkMarkerComponent> ChunkMarkerCompMapper = ComponentMapper
            .getFor(ChunkMarkerComponent.class); //Having ECS code in this class isn't entirely fancy either

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

    private final WorldAccessor worldAccessor;

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
        this.tx = rx * CHUNK_SIZE;
        this.ty = ry * CHUNK_SIZE;
        this.listeners = new ArrayList<>();
        this.tiles = new TileStorage(CHUNK_SIZE, this.tx, this.ty);
        this.tilesBackground = new TileStorage(CHUNK_SIZE, this.tx, this.ty);
        this.tileEntities = new ArrayList<>();
        this.tickables = new ArrayList<>();
        this.entities = new ArrayList<>();
        this.immutableEntities = Collections.unmodifiableList(this.entities);
        this.tickablesForRemoval = new ArrayDeque<>();
        this.regionEntity = new Entity();
        this.regionEntity.add(new ChunkComponent(this));
        this.regionEntity.add(new ChunkRenderComponent());//TMP because server side stuff
        this.regionEntity.add(new RenderComponent(0, "chunk"));
        PhysicsComponent pc = new PhysicsComponent();
        pc.factory = new ChunkPhysics(this);
        this.regionEntity.add(pc);
        this.worldAccessor = worldAccessor;
    }

    private void notifyListeners(TileState state, Tile newTile, Tile oldTile, int gtx, int gty) {
        for (ChunkChangeListener l : this.listeners) {
            l.onTileStateChange(this, state, newTile, oldTile, gtx, gty);
        }
    }

    public void addListener(ChunkChangeListener listener) {
        this.listeners.add(listener);
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
        return this.regionEntity;
    }

    @Deprecated
    public void tileIntersections(Collection<TileState> output, int x, int y, int w, int h,
            Predicate<TileState> predicate) {
        this.tiles.getAABB(output, x, y, w, h, predicate);
    }

    @Deprecated
    public void tileIntersectionsBackground(Collection<TileState> output, int x, int y, int w, int h,
            Predicate<TileState> predicate) {
        this.tilesBackground.getAABB(output, x, y, w, h, predicate);
    }

    @Deprecated
    public void tileAll(Collection<TileState> output, Predicate<TileState> predicate) {
        this.tiles.getAll(output, predicate);
    }

    @Deprecated
    public void tileBackgroundAll(Collection<TileState> output, Predicate<TileState> predicate) {
        this.tilesBackground.getAll(output, predicate);
    }

    public Tile getTile(int tx, int ty) {
        return this.tiles.get(tx, ty).getTile();
    }

    //Not the best solution, but TileState visibility is a problem anyways
    TileState getTileState(int tx, int ty) {
        return this.tiles.get(tx, ty);
    }

    //Maybe save the set for later somehow?

    public Tile setTile(Tile t, int tx, int ty) {
        Objects.requireNonNull(t);
        GameRegistry.TILE_REGISTRY.checkRegistered(t);
        TileState state = this.tiles.get(tx, ty);
        Tile oldTile = state.getTile();
        if (state.getTileEntity() != null) {
            this.tileEntities.remove(state.getTileEntity());
            if (state.getTileEntity() instanceof Tickable) {
                Tickable oldTickable = (Tickable) state.getTileEntity();
                if (this.ticking) {
                    this.tickablesForRemoval.add(oldTickable);
                } else {
                    this.tickables.remove(oldTickable);
                }
            }
            state.setTileEntity(null);
        }
        state.setTile(t);
        if (t.hasTileEntity()) {
            TileEntity te = t.createTileEntity(this.worldAccessor, tx, ty);
            this.tileEntities.add(te);
            state.setTileEntity(te);
            if (te instanceof Tickable) {
                this.tickables.add((Tickable) te);
            }
        }
        notifyListeners(state, t, oldTile, tx, ty);
        //-> neighbour change notifications, but dont notify if the chunk is just being loaded
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
        return oldTile;
    }

    public Tile getBackground(int tx, int ty) {
        return this.tilesBackground.get(tx, ty).getTile();
    }

    public void setTileBackground(Tile t, int tx, int ty) {
        this.tilesBackground.set(t, tx, ty);
    }

    public boolean inBounds(int gtx, int gty) {
        return gtx >= this.tx && gtx < this.tx + CHUNK_SIZE && gty >= this.ty && gty < this.ty + CHUNK_SIZE
                && this.worldAccessor.getWorldBounds().inBounds(gtx, gty);
    }

    public void tick(float time) {
        this.ticking = true;
        this.tickables.forEach((t) -> t.tick(time));
        this.ticking = false;
        while (!this.tickablesForRemoval.isEmpty()) {
            this.tickables.remove(this.tickablesForRemoval.poll());
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
        NBTList tileList = nbtc.getList("tiles");
        NBTList tileEntities = nbtc.getList("tileEntities");
        int cx = getGlobalTileX();
        int cy = getGlobalTileY();
        for (int i = 0; i < tileList.size(); i += 2) {
            int x = (i / 2) / CHUNK_SIZE;
            int y = (i / 2) % CHUNK_SIZE;
            //Foreground tiles
            String id = tileList.getString(i);
            Tile t = GameRegistry.TILE_REGISTRY.getOrDefault(id, Tile.EMPTY);
            setTile(t, cx + x, cy + y);
            //Background tiles
            String idB = tileList.getString(i + 1);
            Tile tB = GameRegistry.TILE_REGISTRY.getOrDefault(idB, Tile.EMPTY);
            setTileBackground(tB, cx + x, cy + y);
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
        NBTList entities = new NBTList(NBTType.Compound);
        NBTList tileEntities = new NBTList(NBTType.Compound);
        int cx = getGlobalTileX();
        int cy = getGlobalTileY();
        for (int i = 0; i < CHUNK_SIZE; i++) {
            for (int j = 0; j < CHUNK_SIZE; j++) {
                TileState st = this.tiles.get(cx + i, cy + j);
                String id = GameRegistry.TILE_REGISTRY.getId(st.getTile());
                tileList.addString(id);
                Tile t = st.getTile();
                if (t.hasTileEntity()) {
                    TileEntity e = st.getTileEntity();
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
        chunkMaster.putList("tiles", tileList);
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
