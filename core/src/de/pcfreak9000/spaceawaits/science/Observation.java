package de.pcfreak9000.spaceawaits.science;

public class Observation {
    
    public static final int ENV_WORLD = 1;
    public static final int ENV_FLAT = 2;
    
    private String displayname = "";
    
    public String getDisplayName() {
        return displayname;
    }
    
    public void setDisplayName(String s) {
        this.displayname = s;
    }
    
    public boolean hasData() {
        return false;
    }
    
    public Object createDataHolder() {
        return null;
    }
}
