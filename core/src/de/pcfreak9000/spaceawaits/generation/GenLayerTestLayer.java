package de.pcfreak9000.spaceawaits.generation;

import com.badlogic.ashley.utils.ImmutableArray;

public class GenLayerTestLayer extends GenLayer<IBiomeGen> implements IGeneratingLayer {
    
    @Override
    protected Parameters[] generateSubNodes(long seed, Parameters parameters) {
        return new Parameters[] {};
    }
    
    @Override
    protected IBiomeGen selectChildFor(long seed, Parameters childParams,
            ImmutableArray<GenInfo> parallelLayersSelected) {
        return null;
    }
    
    @Override
    public Layer generate(long seed, Parameters params) {
        GenInfo[] subs = (GenInfo[]) super.generate(seed, params);
        return new Layer(subs, (LayerParameters) params, seed);
    }
    
}
