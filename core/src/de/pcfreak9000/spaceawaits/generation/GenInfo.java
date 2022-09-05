package de.pcfreak9000.spaceawaits.generation;

public class GenInfo {
    private Object generated;
    private Parameters myParams;
    
    public GenInfo(Object generated, Parameters myParams) {
        this.generated = generated;
        this.myParams = myParams;
    }
    
    public Object getGenerated() {
        return generated;
    }
    
    public Parameters getParams() {
        return myParams;
    }
    
    public boolean hasGenLayer() {
        return (generated instanceof GenLayer<?, ?, ?>);
    }
    
}
