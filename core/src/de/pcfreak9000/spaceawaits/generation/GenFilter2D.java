package de.pcfreak9000.spaceawaits.generation;

public abstract class GenFilter2D<E> {
    
    public abstract void filter(int tx, int ty, FilterCollection<E> stuff);
    
    protected abstract GenFilter2D<E> selectChild(int tx, int ty);
    
    public <T extends GenFilter2D<E>> T getLayer2D(int tx, int ty, Class<T> clazz) {
        if (this.getClass().equals(clazz)) {
            return (T) this;
        }
        GenFilter2D<E> child = selectChild(tx, ty);
        if (child != null) {
            return child.getLayer2D(tx, ty, clazz);
        }
        return null;
    }
}
