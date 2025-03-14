package de.pcfreak9000.spaceawaits.world.breaking;

public class BreakableInfo {
    private boolean canBreak = true;
    private float hardness = 1f;
    private float materialLevel = 0f;
    private String tool;
    
    public float getMaterialLevel() {
        return materialLevel;
    }
    
    public BreakableInfo setMaterialLevel(float materialLevel) {
        this.materialLevel = materialLevel;
        return this;
    }
    
    public BreakableInfo setRequiredTool(String tool) {
        this.tool = tool;
        return this;
    }
    
    public String getRequiredTool() {
        return tool;
    }
    
    public boolean canBreak() {
        return canBreak;
    }
    
    public BreakableInfo setCanBreak(boolean canBreak) {
        this.canBreak = canBreak;
        return this;
    }
    
    public float getHardness() {
        return hardness;
    }
    
    public BreakableInfo setHardness(float hardness) {
        this.hardness = hardness;
        return this;
    }
    
}
