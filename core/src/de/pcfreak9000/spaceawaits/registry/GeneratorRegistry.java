package de.pcfreak9000.spaceawaits.registry;

import java.util.ArrayList;
import java.util.List;

import de.pcfreak9000.spaceawaits.world.gen.WorldSetup;

public class GeneratorRegistry extends GameRegistry<WorldSetup> {
    
    public List<WorldSetup> filtered(Object... filter) {
        List<WorldSetup> filterOutput = new ArrayList<>();
        for (WorldSetup t : this.registered.values()) {
            if (t.hasCapabilities(filter)) {
                filterOutput.add(t);
            }
        }
        return filterOutput;
    }
    
}
