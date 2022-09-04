package de.pcfreak9000.spaceawaits.generation;

public class GenInfo {
    private GenLayer myLayer;
    private Parameters myParams;
    
    public GenInfo(GenLayer myLayer, Parameters myParams) {
        this.myLayer = myLayer;
        this.myParams = myParams;
    }
    
    public GenLayer getLayer() {
        return myLayer;
    }
    
    public Parameters getParams() {
        return myParams;
    }
    
    public GenInfo[] generate(long seed) {
        return this.myLayer.generate(seed, this.myParams);
    }
    
}
