package de.pcfreak9000.spaceawaits.generation;

import de.pcfreak9000.spaceawaits.world.gen.biome.BiomeGenCompBased;

@Deprecated
public class BiomeGenExpander {
    @Deprecated
    public static BiomeGenCompBased[] expand(GenInfo[] layers) {
        BiomeGenCompBased[] subgens = new BiomeGenCompBased[layers.length];
        for (int i = 0; i < subgens.length; i++) {
            GenInfo inf = layers[i];
            Object res = inf.getGenerated();
            if (res instanceof IGeneratingLayer<?, ?>) {
                IGeneratingLayer<?, Object> sublayer = (IGeneratingLayer<?, Object>) res;
                res = sublayer.generate(inf.getParams());
            }
            if (res instanceof BiomeGenCompBased) {
                BiomeGenCompBased ibg = (BiomeGenCompBased) res;
                subgens[i] = ibg;
            } else {
                //Problem   
            }
        }
        return subgens;
    }
    
}
