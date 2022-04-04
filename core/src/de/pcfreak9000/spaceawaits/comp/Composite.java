package de.pcfreak9000.spaceawaits.comp;

import com.badlogic.ashley.utils.ImmutableArray;

public class Composite {
    
    private String id;
    
    private int level;
    
    private ImmutableArray<CompositeData> composition;
    
    Composite(int level, ImmutableArray<CompositeData> comp, String id) {
        this.level = level;
        this.composition = comp;
        this.id = id;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public String getId() {
        return id;
    }
    
    public ImmutableArray<CompositeData> getContents() {
        return this.composition;
    }
    
    @Override
    public String toString() {
        return "Composite [id=" + id + ", level=" + level + "]";
    }
    
}
