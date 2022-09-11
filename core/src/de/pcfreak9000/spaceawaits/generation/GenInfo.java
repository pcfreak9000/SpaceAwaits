package de.pcfreak9000.spaceawaits.generation;

public class GenInfo {
    private Object generated;
    private Object myParams;
    
    public GenInfo(Object generated, Object myParams) {
        this.generated = generated;
        this.myParams = myParams;
    }
    
    public Object getGenerated() {
        return generated;
    }
    
    public Object getParams() {
        return myParams;
    }
    
}
