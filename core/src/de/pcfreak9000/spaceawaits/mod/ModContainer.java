package de.pcfreak9000.spaceawaits.mod;

/**
 * contains a mod
 *
 * @author pcfreak9000
 *
 */
public class ModContainer {
    
    private final Mod mod;
    private final Class<?> mainclass;
    private final Object instance;
    
    public ModContainer(final Class<?> mc, final Mod mod, final Object instance) {
        this.mainclass = mc;
        this.mod = mod;
        this.instance = instance;
    }
    
    public Mod getMod() {
        return this.mod;
    }
    
    public Object getInstance() {
        return this.instance;
    }
    
    public Class<?> getModClass() {
        return this.mainclass;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ModContainer)) {
            return false;
        }
        final ModContainer other = (ModContainer) o;
        if (other.mainclass.getName().equals(this.mainclass.getName())) {
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(this.mod.id());
        b.append(' ');
        b.append('v');
        long[] v = this.mod.version();
        for (int i = 0; i < v.length; i++) {
            b.append(v[i]);
            if (i < v.length - 1) {
                b.append('.');
            }
        }
        return b.toString();
    }
}
