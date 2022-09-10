package de.pcfreak9000.spaceawaits.generation;

public class GenInfo {
    private Object generated;
    private Object parent;
    private Object myParams;
    
    public GenInfo(Object parent, Object generated, Object myParams) {
        this.generated = generated;
        this.myParams = myParams;
        this.parent = parent;
    }
    
    public Object getParent() {
        return parent;
    }
    
    public Object getGenerated() {
        return generated;
    }
    
    public Object getParams() {
        return myParams;
    }
    
}
