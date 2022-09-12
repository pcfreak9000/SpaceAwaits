package de.pcfreak9000.spaceawaits.content.gen;

import de.pcfreak9000.spaceawaits.generation.BiomeGenExpander;
import de.pcfreak9000.spaceawaits.generation.GenInfo;
import de.pcfreak9000.spaceawaits.generation.IGeneratingLayer;
import de.pcfreak9000.spaceawaits.world.gen.biome.BiomeGenCompBased;

public class SpaceSurface extends BiomeGenCompBased {
    
    private IGeneratingLayer<GenInfo[], SpaceSurface> genChildren = new IGeneratingLayer<GenInfo[], SpaceSurface>() {
        
        //List generators;
        
        @Override
        public GenInfo[] generate(SpaceSurface surface) {
            SpaceSurfaceParams params = surface.getParams();
            int someint = params.getHeight() / 3;
            LayerParams lowerLevel = new LayerParams(surface, 0, someint);
            LayerParams higherLevel = new LayerParams(surface, someint, someint);
            //for each LayerParam pick a generator from generators
            return new GenInfo[] { new GenInfo(LayerGenerator.GENERATOR, lowerLevel),
                    new GenInfo(LayerGenerator.GENERATOR, higherLevel) };
        }
    };
    private BiomeGenCompBased[] subs;
    private SpaceSurfaceParams params;
    
    public SpaceSurface(SpaceSurfaceParams params) {
        super(null);
        this.params = params;
        addComponent(HeightComponent.class, new HeightComponent());
        //setup SpaceSurface children
        subs = BiomeGenExpander.expand(genChildren.generate(this));
    }
    
    public SpaceSurfaceParams getParams() {
        return params;
    }
    
    @Override
    public BiomeGenCompBased getLeaf(int tx, int ty) {
        for (int i = 0; i < subs.length - 1; i++) {
            Layer l = (Layer) subs[i];
            if (ty < l.getParams().getMeanY() + l.getParams().getMeanThickness()) {
                return l;
            }
        }
        return subs[subs.length - 1];//default to the topmost layer
    }
    
}
