package de.pcfreak9000.spaceawaits.generation;

import com.badlogic.ashley.utils.ImmutableArray;

public class GenLayerTestLayer implements IGeneratingLayer<Layer, LayerParameters> {
    
    GenLayer<IBiomeGen, LayerParameters, Parameters> layerlayer = new GenLayer<IBiomeGen, LayerParameters, Parameters>() {
        
        @Override
        protected Parameters[] generateSubNodes(LayerParameters parameters) {
            return new Parameters[] {};
        }
        
        @Override
        protected IBiomeGen getChildFor(LayerParameters p, Parameters childParams,
                ImmutableArray<GenInfo> parallelLayersSelected) {
            return null;
        }
        
    };
    
    @Override
    public Layer generate(LayerParameters params) {
        GenInfo[] subs = layerlayer.generate(params);
        return new Layer(subs, params);
    }
    
}
