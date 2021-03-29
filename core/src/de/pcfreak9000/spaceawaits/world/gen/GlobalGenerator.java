package de.pcfreak9000.spaceawaits.world.gen;

import de.pcfreak9000.spaceawaits.world.Global;

public interface GlobalGenerator {
    
    void populateGlobal(Global gl);
    
    default void repopulateGlobal(Global gl) {
    }
}
