package de.pcfreak9000.spaceawaits.world.chunk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
    private int size;
    
    private TileLayer layer;
    
    private final List<Tickable> tickables;
    
    private final Queue<Tickable> tickablesForRemoval;
    private boolean ticking = false;
    
    public TileStorage(World world, int size, int tx, int ty, TileLayer layer) {
        this.tx = tx;
        this.ty = ty;
        this.size = size;
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
    
    public void getAABB(Collection<TileState> output, int x, int y, int w, int h, Predicate<TileState> predicate) {
        int xStart = Math.max(0, x - this.tx);
        int yStart = Math.max(0, y - this.ty);
        for (int i = xStart; i <= xStart + w && i < size; i++) {
            for (int j = yStart; j <= yStart + h && j < size; j++) {
                TileState t = tileArray[i][j];
                if ((predicate == null || predicate.test(t))) {
                    output.add(t);
                }
            }
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
    
    public void getAll(Collection<TileState> list, Predicate<TileState> predicate) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                TileState t = tileArray[i][j];
                if (t != null && (predicate == null || predicate.test(t))) {
                    list.add(t);
                }
            }
        }
    }
    
    public void execute(Consumer<TileState> function) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                TileState t = tileArray[i][j];
                if (t != null) {
                    function.accept(t);
                }
            }
        }
    }
    
}
