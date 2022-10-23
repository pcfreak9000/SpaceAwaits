package de.pcfreak9000.spaceawaits.world.chunk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTList;
import de.pcfreak9000.nbt.NBTSmartIntList;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.nbt.NBTType;
import de.pcfreak9000.spaceawaits.module.IModuleTileEntity;
import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.save.ChunkDict;
import de.pcfreak9000.spaceawaits.serialize.AnnotationSerializer;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.ITileEntity;
import de.pcfreak9000.spaceawaits.world.tile.Tickable;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class TileStorage implements Tickable {
    
    private final World world;
    
    private int size;
    private TileState[][] tileArray;
    private int tx;
    private int ty;
    
    private TileLayer layer;
    
    private final List<Tickable> tickables;
    
    private final Queue<Tickable> tickablesForRemoval;
    private boolean ticking = false;
    
    public TileStorage(World world, int size, int tx, int ty, TileLayer layer) {
        this.tx = tx;
        this.ty = ty;
        this.world = world;
        this.layer = layer;
        this.size = size;
        this.tileArray = new TileState[size][size];
        this.tickablesForRemoval = new ArrayDeque<>();
        this.tickables = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.tileArray[i][j] = new TileState();
                this.tileArray[i][j].setTile(Tile.NOTHING);
            }
        }
    }
    
    @Override
    public void tick(float time, long tick) {
        this.ticking = true;
        this.tickables.forEach((t) -> t.tick(time, tick));
        this.ticking = false;
        while (!this.tickablesForRemoval.isEmpty()) {
            this.tickables.remove(this.tickablesForRemoval.poll());
        }
    }
    
    public TileState get(int tileX, int tileY) {
        return tileArray[tileX - this.tx][tileY - this.ty];
    }
    
    public TileState set(Tile t, int tileX, int tileY) {
        TileState state = get(tileX, tileY);
        if (state.getTileEntity() != null) {
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
        tileArray[tileX - this.tx][tileY - this.ty].setTile(t);
        if (t.hasModule(IModuleTileEntity.ID)) {
            IModuleTileEntity temod = t.getModule(IModuleTileEntity.ID);
            ITileEntity te = temod.createTileEntity(this.world, tx, ty, this.layer);
            state.setTileEntity(te);
            if (te instanceof Tickable) {
                this.tickables.add((Tickable) te);
            }
        }
        return state;
    }
    
    public void deserialize(ChunkDict dict, NBTCompound in, Chunk chunk) {
        NBTList tileList = in.getList("tiles");
        for (int i = 0; i < tileList.size(); i++) {
            int x = (i) / size;
            int y = (i) % size;
            int idN = (int) tileList.getNumberAutocast(i);
            String id = dict.getStringFrom(idN);
            Tile t = Registry.TILE_REGISTRY.getOrDefault(id, Tile.NOTHING);
            chunk.setTile(tx + x, ty + y, layer, t);
        }
        NBTList tileEntities = in.getList("tileEntities");
        for (NBTTag tet : tileEntities.getContent()) {
            NBTCompound comp = (NBTCompound) tet;
            int x = (int) comp.getIntegerSmart("x");
            int y = (int) comp.getIntegerSmart("y");
            TileState state = tileArray[x][y];
            if (state.getTile().hasModule(IModuleTileEntity.ID)) {//Possibly check if the tileentitytype matches, in the future the default tile could change etc...
                AnnotationSerializer.deserialize(state.getTileEntity(), comp);
            }
        }
    }
    
    double d = 0;
    double c = 0;
    
    public NBTCompound serialize(ChunkDict dict) {
        NBTList tileEntities = new NBTList(NBTType.Compound);
        NBTSmartIntList list = new NBTSmartIntList();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                TileState st = tileArray[i][j];
                String id = Registry.TILE_REGISTRY.getId(st.getTile());
                int idN = dict.getIdFor(id);
                //array.add(idN);
                list.addSmartInt(idN);
                if (st.getTile().hasModule(IModuleTileEntity.ID)) {
                    ITileEntity te = st.getTileEntity();
                    NBTCompound tecomp = AnnotationSerializer.serialize(te);
                    if (!tecomp.isEmpty()) {
                        tecomp.putIntegerSmart("x", i);
                        tecomp.putIntegerSmart("y", j);
                        tileEntities.addCompound(tecomp);
                    }
                }
            }
        }
        NBTCompound tilestorageMaster = new NBTCompound();
        tilestorageMaster.putList("tiles", list);
        tilestorageMaster.putList("tileEntities", tileEntities);
        return tilestorageMaster;
    }
    
}
