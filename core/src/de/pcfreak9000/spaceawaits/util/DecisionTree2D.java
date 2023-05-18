package de.pcfreak9000.spaceawaits.util;

public abstract class DecisionTree2D<T> {
    
    private T leaf;
    
    public DecisionTree2D(T leaf) {
        this.leaf = leaf;
    }
    
    public T get(int x, int y) {
        if (leaf != null) {
            return leaf;
        }
        return getSubnode(x, y).get(x, y);
    }
    
    protected abstract DecisionTree2D<T> getSubnode(int x, int y);
    
}
