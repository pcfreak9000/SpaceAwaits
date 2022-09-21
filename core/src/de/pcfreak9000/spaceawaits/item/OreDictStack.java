package de.pcfreak9000.spaceawaits.item;

public class OreDictStack {
    private String name;
    private int count;
    
    public OreDictStack(String name, int count) {
        this.name = name;
        this.count = count;
        if (count < 1) {
            throw new IllegalArgumentException("count < 1");
        }
    }
    
    public String getName() {
        return name;
    }
    
    public int getCount() {
        return count;
    }
}
