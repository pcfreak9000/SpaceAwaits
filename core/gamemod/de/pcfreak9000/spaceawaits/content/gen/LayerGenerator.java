package de.pcfreak9000.spaceawaits.content.gen;

import de.pcfreak9000.spaceawaits.generation.IGeneratingLayer;
import de.pcfreak9000.spaceawaits.world.gen.biome.Biome;
import layerteststuff.TestBiome;

public class LayerGenerator implements IGeneratingLayer<Layer, LayerParams> {
    
    public static final LayerGenerator GENERATOR = new LayerGenerator();
    
    private Biome top = new TestBiome(false);
    private Biome bottom = new TestBiome(true);
    
    @Override
    public Layer generate(LayerParams params) {
        if (params.getMeanY() == 0) {
            return new Layer(params, bottom);
        }
        return new Layer(params, top);
    }
    
}
