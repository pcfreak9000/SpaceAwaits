package layerteststuff;

import de.pcfreak9000.spaceawaits.world.gen.biome.Biome;
import de.pcfreak9000.spaceawaits.world.gen.biome.BiomeGenCompBased;

public class TestBiomeGenerator extends BiomeGenCompBased {
    
    private TestBiome testBiome = new TestBiome();
    
    public TestBiomeGenerator(long seed) {
        super(null);
    }
    
    @Override
    public Biome getBiome(int tx, int ty) {
        return testBiome;
    }
    
}
