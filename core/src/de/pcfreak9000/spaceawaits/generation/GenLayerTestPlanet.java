package de.pcfreak9000.spaceawaits.generation;

import com.badlogic.ashley.utils.ImmutableArray;

public class GenLayerTestPlanet extends GenLayer {
    
    private GenLayerTestLayer sublayer = new GenLayerTestLayer();
    
    public Planet generateS(long seed, Parameters parameters) {
        return new Planet(generate(seed, parameters), (PlanetParameters) parameters, seed);
    }
    
    @Override
    protected Parameters[] generateSubNodes(long seed, Parameters parameters) {
        return new Parameters[] { new LayerParameters() };
    }
    
    @Override
    protected GenLayer selectChildFor(long seed, Parameters childParams,
            ImmutableArray<GenInfo> parallelLayersSelected) {
        return sublayer;
    }
    
}
