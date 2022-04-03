package de.pcfreak9000.spaceawaits.comp;

import com.badlogic.ashley.utils.ImmutableArray;

public class Composite {
    
    private int level;
    
    private ImmutableArray<CompositeData> composition;
    
    Composite(int level, ImmutableArray<CompositeData> comp) {
        this.level = level;
        this.composition = comp;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public ImmutableArray<CompositeData> getContents() {
        return this.composition;
    }
}
