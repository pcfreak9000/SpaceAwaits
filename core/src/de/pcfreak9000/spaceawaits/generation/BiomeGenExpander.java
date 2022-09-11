package de.pcfreak9000.spaceawaits.generation;

import de.pcfreak9000.spaceawaits.world.gen.biome.IBiomeGen;

public class BiomeGenExpander {
    
    public static IBiomeGen[] expand(GenInfo[] layers) {
        IBiomeGen[] subgens = new IBiomeGen[layers.length];
        for (int i = 0; i < subgens.length; i++) {
            GenInfo inf = layers[i];
            Object res = inf.getGenerated();
            if (res instanceof IGeneratingLayer<?, ?>) {
                IGeneratingLayer<?, Object> sublayer = (IGeneratingLayer<?, Object>) res;
                res = sublayer.generate(inf.getParams());
            }
            if (res instanceof IBiomeGen) {
                IBiomeGen ibg = (IBiomeGen) res;
                subgens[i] = ibg;
            } else {
                //Problem   
            }
        }
        return subgens;
    }
    
}
