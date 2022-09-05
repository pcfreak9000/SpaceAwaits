package de.pcfreak9000.spaceawaits.generation;

import com.badlogic.ashley.utils.ImmutableArray;

public class GenLayerTestPlanet implements IGeneratingLayer<Planet, PlanetParameters> {
    
    private GenLayerTestLayer sublayer = new GenLayerTestLayer();
    
    private GenLayer<IGeneratingLayer<?, ?>, PlanetParameters, Parameters> planetlayer = new GenLayer<IGeneratingLayer<?, ?>, PlanetParameters, Parameters>() {
        
        @Override
        protected Parameters[] generateSubNodes(PlanetParameters parameters) {
            return new Parameters[] { new LayerParameters() };
        }
        
        @Override
        protected IGeneratingLayer<?, ?> getChildFor(PlanetParameters myparams, Parameters childParams,
                ImmutableArray<GenInfo> parallelLayersSelected) {
            return sublayer;
        }
    };
    
    @Override
    public Planet generate(PlanetParameters params) {
        return new Planet(planetlayer.generate(params), params);
    }
    
}
