package de.pcfreak9000.spaceawaits.generation;

public class Gen2DDivider<T> implements IGen2D<T> {
    
    private IGenInt1D divider;
    private IGen2D<T> higher, lower;
    
    public Gen2DDivider(IGenInt1D divider, IGen2D<T> lower, IGen2D<T> higher) {
        this.divider = divider;
        this.lower = lower;
        this.higher = higher;
    }
    
    @Override
    public T generate(int tx, int ty) {
        IGen2D<T> selected;
        if (divider.generate(tx) - ty <= 0) {
            selected = higher;
        } else {
            selected = lower;
        }
        if (selected != null) {
            return selected.generate(tx, ty);
        }
        return null;
    }
    
}
