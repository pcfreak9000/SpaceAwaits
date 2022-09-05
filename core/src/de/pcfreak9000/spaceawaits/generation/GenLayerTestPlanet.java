package de.pcfreak9000.spaceawaits.generation;

import com.badlogic.ashley.utils.ImmutableArray;

public class GenLayerTestPlanet extends GenLayer<GenLayer<?>> implements IGeneratingLayer {
    
    private GenLayerTestLayer sublayer = new GenLayerTestLayer();
    
    @Override
    protected Parameters[] generateSubNodes(long seed, Parameters parameters) {
        return new Parameters[] { new LayerParameters() };
    }
    
    @Override
    protected GenLayer<?> getChildFor(long seed, Parameters childParams,
            ImmutableArray<GenInfo> parallelLayersSelected) {
        return sublayer;
    }
    
    @Override
    public Planet generate(long seed, Parameters params) {
        return new Planet((GenInfo[]) super.generate(seed, params), (PlanetParameters) params, seed);
    }
    
}
