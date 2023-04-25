package de.pcfreak9000.spaceawaits.content.gen;
@Deprecated
public class LayerParams {
    
    private SpaceSurface parent;
    private int ypos;
    private int meanHeight;
    
    public LayerParams(SpaceSurface parent, int y, int height) {
        this.parent = parent;
        this.ypos = y;
        this.meanHeight = height;
    }
    
    public int getMeanY() {
        return ypos;
    }
    
    public int getMeanThickness() {
        return meanHeight;
    }
    
    public SpaceSurface getParent() {
        return this.parent;
    }
}
