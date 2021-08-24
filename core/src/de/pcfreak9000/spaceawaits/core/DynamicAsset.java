package de.pcfreak9000.spaceawaits.core;

public interface DynamicAsset {
    
    //save scale: TBD
    //world/unchunk scale: create when world is created (and set as rendering, for the future when there might be multiple worlds)
    //                     dispose when world is unloaded (or when set as not rendering?)
    //entity/chunk scale: dynamicassetcomponent?
    
    void create();
    void dispose();
}
