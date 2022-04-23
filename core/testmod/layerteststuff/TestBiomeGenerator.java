package layerteststuff;

import de.pcfreak9000.spaceawaits.world.gen.biome.Biome;
import de.pcfreak9000.spaceawaits.world.gen.biome.BiomeGenerator;

public class TestBiomeGenerator extends BiomeGenerator {
    
    private TestBiome testBiome = new TestBiome();
    
    public TestBiomeGenerator(long seed) {
        super(seed);
    }
    
    @Override
    public Biome getBiome(int tx, int ty) {
        return testBiome;
    }
    
}
