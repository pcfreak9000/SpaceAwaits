package de.pcfreak9000.spaceawaits.generation;

import java.util.function.Supplier;

import com.sudoplay.joise.module.Module;

public class NoiseGenerator {
    private ThreadLocal<Module> module;
    
    public NoiseGenerator(Supplier<Module> moduleGen) {
        this.module = ThreadLocal.withInitial(moduleGen);
    }
    
    public Module get() {
        return this.module.get();
    }
}
