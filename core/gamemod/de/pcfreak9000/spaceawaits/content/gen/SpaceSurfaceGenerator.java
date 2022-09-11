package de.pcfreak9000.spaceawaits.content.gen;

import de.pcfreak9000.spaceawaits.generation.GenInfo;
import de.pcfreak9000.spaceawaits.generation.IGeneratingLayer;
import de.pcfreak9000.spaceawaits.world.WorldBounds;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;
import de.pcfreak9000.spaceawaits.world.gen.biome.BiomeChunkGenerator;

public class SpaceSurfaceGenerator implements IGeneratingLayer<WorldPrimer, SpaceSurfaceParams> {
    
    public static class SpaceSurfaceSubParams {
        private SpaceSurfaceParams params;
        private SpaceSurface spaceSurface;
        
        public SpaceSurfaceSubParams(SpaceSurfaceParams params, SpaceSurface spaceSurface) {
            this.params = params;
            this.spaceSurface = spaceSurface;
        }
        
        public SpaceSurfaceParams getParams() {
            return params;
        }
        
        public SpaceSurface getSpaceSurface() {
            return spaceSurface;
        }
        
    }
    
    private IGeneratingLayer<GenInfo[], SpaceSurfaceSubParams> genChildren = new IGeneratingLayer<GenInfo[], SpaceSurfaceSubParams>() {
        
        @Override
        public GenInfo[] generate(SpaceSurfaceSubParams params) {
            LayerParams p = new LayerParams(params.getSpaceSurface());
            return null;
        }
    };
    
    @Override
    public WorldPrimer generate(SpaceSurfaceParams params) {
        //setup SpaceSurface
        SpaceSurface surface = new SpaceSurface();
        //setup SpaceSurface children
        surface.expand(genChildren.generate(new SpaceSurfaceSubParams(params, surface)));
        //setup worldprimer
        WorldPrimer p = new WorldPrimer();
        p.setWorldBounds(new WorldBounds(params.getWidth(), params.getHeight()));
        p.setChunkGenerator(new BiomeChunkGenerator(surface, params.getSeed()));
        return p;
    }
    
}
