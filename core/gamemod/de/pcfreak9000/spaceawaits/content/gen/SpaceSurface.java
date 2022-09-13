package de.pcfreak9000.spaceawaits.content.gen;

import java.util.Random;

import com.badlogic.gdx.math.RandomXS128;

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
    
    private LayerHeightVariation[] vars;
    
    private HeightComponent height;
    
    public SpaceSurface(SpaceSurfaceParams params) {
        super(null);
        this.params = params;
        this.height = new HeightComponent(params.getSeed());
        addComponent(HeightComponent.class, this.height);
        //setup SpaceSurface children
        subs = BiomeGenExpander.expand(genChildren.generate(this));
        vars = new LayerHeightVariation[subs.length - 1];
        Random r = new RandomXS128(params.getSeed());
        for (int i = 0; i < vars.length; i++) {
            vars[i] = new LayerHeightVariation(params.getSeed() + 1 + i, 15 + r.nextInt(5));
        }
    }
    
    public SpaceSurfaceParams getParams() {
        return params;
    }
    
    @Override
    public BiomeGenCompBased getLeaf(int tx, int ty) {
        for (int i = 0; i < subs.length - 1; i++) {
            Layer l = (Layer) subs[i];
            if (ty < l.getParams().getMeanY() + l.getParams().getMeanThickness() + vars[i].getVariation(tx, ty)) {
                return l;
            }
        }
        return subs[subs.length - 1];//default to the topmost layer
    }
    
}
