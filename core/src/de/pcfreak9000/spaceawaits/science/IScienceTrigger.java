package de.pcfreak9000.spaceawaits.science;

public interface IScienceTrigger {
    
    public boolean isComplete(Science science);
    
    public void triggerComplete(Science science);
}
