package de.pcfreak9000.spaceawaits.generation;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;

public class FilterCollection<E> {
    
    private Bits markDisabled;
    private Array<E> array;
    private int disabledCount;
    private int lastDisabled;
    
    public FilterCollection(Array<E> backing) {
        this.markDisabled = new Bits();
        this.array = backing;
    }
    
    public void disable(int index) {
        markDisabled.set(index);
        disabledCount++;
        lastDisabled = index;
    }
    
    public boolean isDisabled(int index) {
        return markDisabled.get(index);
    }
    
    public E get(int index) {
        return array.get(index);
    }
    
    public int size() {
        return array.size;
    }
    
    public int getLastDisabledIndex() {
        return lastDisabled;
    }
    
    public boolean hasAnyEnabled() {
        return disabledCount < array.size;
    }
    
    public void collectEnabled(Array<E> out) {
        for (int i = 0; i < array.size; i++) {
            if (!markDisabled.get(i)) {
                out.add(array.get(i));
            }
        }
    }
    
    public void reset() {
        markDisabled.clear();
        disabledCount = 0;
        lastDisabled = -1;
    }
    
}
