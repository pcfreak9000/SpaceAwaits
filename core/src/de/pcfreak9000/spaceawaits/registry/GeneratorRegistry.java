package de.pcfreak9000.spaceawaits.registry;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import de.pcfreak9000.spaceawaits.generation.IGeneratingLayer;
import de.pcfreak9000.spaceawaits.world.gen.GeneratorSettings;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;

public class GeneratorRegistry extends Registry<IGeneratingLayer<?, ?>> {
    
    private Set<IGeneratingLayer<WorldPrimer, GeneratorSettings>> gens = new LinkedHashSet<>();
    
    public void registerWorldGen(String id, IGeneratingLayer<WorldPrimer, GeneratorSettings> sets) {
        register(id, sets);
        this.gens.add(sets);
        //isnt re-register safe, soo...
    }
    
    public Set<IGeneratingLayer<WorldPrimer, GeneratorSettings>> getGens() {
        return Collections.unmodifiableSet(gens);
    }
    
}
