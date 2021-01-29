package de.pcfreak9000.spaceawaits.tileworld;

import java.util.HashSet;
import java.util.Set;

import de.omnikryptec.math.Weighted;

/**
 * TileWorld generator. Capabilities of that generator.
 *
 * @author pcfreak9000
 *
 */
public abstract class WorldGenerator implements Weighted {
    
    public static enum GeneratorCapabilitiesBase {
        LVL_ENTRY/*, ADRESSABLE_PORTAL*/;
    }
    
    protected final Set<Object> CAPS = new HashSet<>();
    
    public abstract World generateWorld(long seed);
    
    public WorldGenerator() {
        initCaps();
    }
    
    //for anonymous inner classes that can not use a constructor
    protected void initCaps() {
    }
    
    @Override
    public int getWeight() {
        return 100;
    }
    
    public final boolean hasCapabilities(Object... names) {
        for (Object o : names) {
            if (!this.CAPS.contains(o)) {
                return false;
            }
        }
        return true;
    }
    
}
