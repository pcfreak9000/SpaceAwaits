package de.pcfreak9000.spaceawaits.generation;

public abstract class GenFilter1D<E> {
    
    public abstract void filter(int i, FilterCollection<E> stuff);
    
    protected abstract GenFilter1D<E> selectChild(int i);
    
    public <T extends GenFilter1D<E>> T getLayer1D(int i, Class<T> clazz) {
        if (this.getClass().equals(clazz)) {
            return (T) this;
        }
        GenFilter1D<E> child = selectChild(i);
        if (child != null) {
            return child.getLayer1D(i, clazz);
        }
        return null;
    }
}
