package de.pcfreak9000.spaceawaits.generation;

@Deprecated
public class GenLayerTestLayer implements IGeneratingLayer<Layer, LayerParameters> {
    
    IGeneratingLayer<GenInfo[], LayerParameters> layerlayer = new IGeneratingLayer<GenInfo[], LayerParameters>() {
        
        @Override
        public GenInfo[] generate(LayerParameters params) {
            return null;
        }
    };
    
    @Override
    public Layer generate(LayerParameters params) {
        GenInfo[] subs = layerlayer.generate(params);
        return new Layer(subs, params);
    }
    
}
