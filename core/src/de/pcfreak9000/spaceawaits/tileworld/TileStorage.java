package de.pcfreak9000.spaceawaits.tileworld;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

import de.pcfreak9000.spaceawaits.tileworld.tile.Tile;
import de.pcfreak9000.spaceawaits.tileworld.tile.TileState;

public class TileStorage {
    
    private TileState[][] tileArray;
    private int tx;
    private int ty;
    private int size;
    
    public TileStorage(int size, int tx, int ty) {
        this.tx = tx;
        this.ty = ty;
        this.size = size;
        this.tileArray = new TileState[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.tileArray[i][j] = new TileState(Tile.EMPTY, this.tx + i, this.ty + j);
            }
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
    
    public TileState set(TileState tileState, int tileX, int tileY) {
        TileState old = get(tileX, tileY);
        tileArray[tileX - this.tx][tileY - this.ty] = tileState;
        return old;
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
