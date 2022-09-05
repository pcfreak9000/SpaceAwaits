package de.pcfreak9000.spaceawaits.generation;

public class BiomeGenExpander {
    
    private IBiomeGen[] subgens;
    
    public BiomeGenExpander(GenInfo[] layers) {
        expand(layers);
    }
    
    private void expand(GenInfo[] layers) {
        subgens = new IBiomeGen[layers.length];
        for (int i = 0; i < subgens.length; i++) {
            GenInfo inf = layers[i];
            if (inf.hasGenLayer()) {
                GenLayer<?, Parameters, ?> sublayer = (GenLayer<?, Parameters, ?>) inf.getGenerated();
                Object res = sublayer.generate(inf.getParams());
                if (res instanceof IBiomeGen) {
                    IBiomeGen ibg = (IBiomeGen) res;
                    subgens[i] = ibg;
                } else {
                    //Problem   
                }
            } else {
                if (inf.getGenerated() instanceof IBiomeGen) {
                    subgens[i] = (IBiomeGen) inf.getGenerated();
                } else {
                    //Problem
                }
            }
        }
    }
    
    public int getSubCount() {
        return subgens.length;
    }
    
    public IBiomeGen getSubBiomeGen(int index) {
        return subgens[index];
    }
    
}
