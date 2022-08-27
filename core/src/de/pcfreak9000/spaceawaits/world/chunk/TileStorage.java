package de.pcfreak9000.spaceawaits.world.chunk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.ITileEntity;
import de.pcfreak9000.spaceawaits.world.tile.Tickable;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class TileStorage implements Tickable {
    
    private final World world;
    
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
        if (t.hasTileEntity()) {
            ITileEntity te = t.createTileEntity(this.world, tx, ty, this.layer);
            state.setTileEntity(te);
            if (te instanceof Tickable) {
                this.tickables.add((Tickable) te);
            }
        }
        return state;
    }
    
}
