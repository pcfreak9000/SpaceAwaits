package de.pcfreak9000.spaceawaits.generation;
@Deprecated
public class GenLayerTestPlanet implements IGeneratingLayer<Planet, PlanetParameters> {
    
    private GenLayerTestLayer sublayer = new GenLayerTestLayer();
    
    private IGeneratingLayer<GenInfo[], PlanetParameters> planetlayer = new IGeneratingLayer<GenInfo[], PlanetParameters>() {
        
        @Override
        public GenInfo[] generate(PlanetParameters params) {
            //Parameters[] childParams = new Parameters[0];
            //Array<GenInfo> sublayers = new Array<>(true, childParams.length, GenInfo.class);
            //for (Parameters p : childParams) {
            //    Object child = null;
            //    sublayers.add(new GenInfo(GenLayerTestPlanet.this, child, p));
            //}
            //return sublayers.items;
            return new GenInfo[] { new GenInfo(null, sublayer, new LayerParameters()) };
        }
    };
    
    @Override
    public Planet generate(PlanetParameters params) {
        return new Planet(planetlayer.generate(params), params);
    }
    
}
