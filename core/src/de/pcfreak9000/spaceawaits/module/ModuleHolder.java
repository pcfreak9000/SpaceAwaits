package de.pcfreak9000.spaceawaits.module;

import com.badlogic.ashley.utils.Bag;
import com.badlogic.gdx.utils.Bits;

public class ModuleHolder {
    
    private Bag<IModule> modules = new Bag<>();
    private Bits moduleBits = new Bits();
    
    public void addModule(ModuleID addAs, IModule m) {
        if (!addAs.getModuleClass().isAssignableFrom(m.getClass())) {
            throw new IllegalArgumentException();
        }
        modules.set(addAs.getIndex(), m);
        moduleBits.set(addAs.getIndex());
    }
    
    public boolean hasModule(ModuleID id) {
        return moduleBits.get(id.getIndex());
    }
    
    public <T extends IModule> T getModule(ModuleID id) {
        return (T) modules.get(id.getIndex());
    }
    
}
