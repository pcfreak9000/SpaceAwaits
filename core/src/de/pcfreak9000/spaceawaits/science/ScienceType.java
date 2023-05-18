package de.pcfreak9000.spaceawaits.science;

import de.pcfreak9000.spaceawaits.registry.Registry;

public class ScienceType {
    
    public static final Registry<ScienceType> REGISTRY = new Registry<>();
    
    public static final ScienceType WITNESS = new ScienceType(0);
    public static final ScienceType BASIC_INTERACTION = new ScienceType(1);
    
    static {
        REGISTRY.register("witness", WITNESS);
        REGISTRY.register("basicinteraction", BASIC_INTERACTION);
    }
    
    private int tier;
    
    public ScienceType(int tier) {
        this.tier = tier;
    }
    
    public int getTier() {
        return tier;
    }
}
