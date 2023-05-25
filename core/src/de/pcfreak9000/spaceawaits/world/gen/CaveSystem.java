package de.pcfreak9000.spaceawaits.world.gen;

import java.util.HashMap;
import java.util.Map;

import de.pcfreak9000.spaceawaits.generation.GenerationDataComponent;
import de.pcfreak9000.spaceawaits.generation.IGen2D;
import de.pcfreak9000.spaceawaits.generation.NoiseGenerator;
import de.pcfreak9000.spaceawaits.util.IntCoordKey;
import de.pcfreak9000.spaceawaits.util.SpecialCache;
import de.pcfreak9000.spaceawaits.util.Util;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

public class CaveSystem implements GenerationDataComponent {
    
    private long seed;
    
    private Map<Class<? extends CaveBiome>, Data> noiseGens;//Hmmmm... use some cache instead??
    
    private IGen2D<CaveBiome> caveBiomeGen;
    
    private static class Data {
        SpecialCache<IntCoordKey, int[][]> scache;
        NoiseGenerator noiseGen;
    }
    
    public CaveSystem(long seed, IGen2D<CaveBiome> caveBiomeGen) {
        this.seed = seed;
        this.noiseGens = new HashMap<>();
        this.caveBiomeGen = caveBiomeGen;
    }
    
    public boolean isCave(int tx, int ty) {
        CaveBiome cavebiome = caveBiomeGen.generate(tx, ty);
        Data data = noiseGens.get(cavebiome.getClass());
        if (data == null) {
            data = new Data();
            data.noiseGen = cavebiome.getNoiseGenCreator().apply(seed);
            NoiseGenerator noiseGen = data.noiseGen;
            data.scache = new SpecialCache<>(60, 55, (key) -> {
                return Util.smoothCA(noiseGen, key.getX() * Chunk.CHUNK_SIZE, key.getY() * Chunk.CHUNK_SIZE,
                        Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE, cavebiome.getSmoothRule(), cavebiome.getMinSolidCount(),
                        cavebiome.getIterations(), cavebiome.getThreshold());
            }, null);
            noiseGens.put(cavebiome.getClass(), data);
        }
        IntCoordKey key = new IntCoordKey(Chunk.toGlobalChunk(tx), Chunk.toGlobalChunk(ty));
        int[][] ints = data.scache.getOrFresh(key);
        //TODO here we would need interpolation
        return ints[tx - key.getX() * Chunk.CHUNK_SIZE][ty - key.getY() * Chunk.CHUNK_SIZE] == 0;
    }
    
}
