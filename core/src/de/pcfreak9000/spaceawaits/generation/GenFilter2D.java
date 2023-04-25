package de.pcfreak9000.spaceawaits.generation;

import java.util.Objects;

public abstract class GenFilter2D<E> {
    
    protected String tag;
    
    protected abstract void filterFlat(int tx, int ty, FilterCollection<E> stuff);
    
    protected GenFilter2D<E> selectChild(int tx, int ty) {
        return null;
    }
    
    public void filter(int tx, int ty, FilterCollection<E> stuff) {
        filterFlat(tx, ty, stuff);
        GenFilter2D<E> child = selectChild(tx, ty);
        if (child != null) {
            child.filter(tx, ty, stuff);
        }
    }
    
    public <T extends GenFilter2D<E>> T getSubFilter2D(int tx, int ty, Class<T> clazz, String tag) {
        if (this.getClass().equals(clazz) && (tag == null || Objects.equals(tag, this.tag))) {
            return (T) this;
        }
        GenFilter2D<E> child = selectChild(tx, ty);
        if (child != null) {
            return child.getSubFilter2D(tx, ty, clazz, tag);
        }
        return null;
    }
}
