package de.pcfreak9000.spaceawaits.world.gen;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.generation.GenerationDataComponent;
import de.pcfreak9000.spaceawaits.generation.IGen2D;
import de.pcfreak9000.spaceawaits.generation.NoiseGenerator;
import de.pcfreak9000.spaceawaits.util.SpecialCache2D;
import de.pcfreak9000.spaceawaits.util.Util;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;

public class CaveSystem implements GenerationDataComponent {
    
    public static final int NO_CAVE = 1;
    public static final int CAVE = 0;
    
    private static final int MONO_CAVEBIOME_CHECK_RADIUS = 5;
    
    private long seed;
    
    private Map<CaveBiome, Data> noiseGens;//Hmmmm... use some cache instead??
    
    private IGen2D<CaveBiome> caveBiomeGen;
    
    private static class Data {
        SpecialCache2D<int[][]> scache;
        NoiseGenerator noiseGen;
    }
    
    //Overall, is the caching here efficient???? We could also cache isCave? or getCaveRawPercentage?......
    
    public CaveSystem(long seed, IGen2D<CaveBiome> caveBiomeGen) {
        this.seed = seed;
        this.noiseGens = new HashMap<>();
        this.caveBiomeGen = caveBiomeGen;
    }
    
    public boolean isCave(int tx, int ty) {
        //We could make MONO_CAVE... a max and below the max, let biomes set the range??? but performance, so the max would be relatively low anyways....
        float dist = checkMonoBiome(tx, ty, MONO_CAVEBIOME_CHECK_RADIUS);
        if (dist == Float.POSITIVE_INFINITY) {
            return getCaveRaw(tx, ty) == CAVE;
        }
        float invdist = Mathf.clamp(MONO_CAVEBIOME_CHECK_RADIUS - dist, 0, MONO_CAVEBIOME_CHECK_RADIUS);
        int invdisti = Mathf.ceili(invdist);
        //This seems to work somewhat well, although we could tweak the number of iterations?
        return getCaveRawPercentage(tx, ty, invdisti) >= 0.5;
        //in smoothCA, 1f- is important
        //        return Util.smoothCA((x, y) -> 1f - getCaveRawPercentage(x, y, invdisti), tx, ty, 1, 1, Direction.MOORE_NEIGHBOURS, 4,
        //                0, 0.5)[0][0] == CAVE;
    }
    
    public CaveBiome getCaveBiome(int tx, int ty) {
        return caveBiomeGen.generate(tx, ty);
    }
    
    private int getCaveRaw(int tx, int ty) {
        if (tx < 0 || ty < 0) {
            return NO_CAVE;//Eh, this sucks... this is because toGlobalChunk cant handle negative numbers
        }
        CaveBiome cavebiome = caveBiomeGen.generate(tx, ty);//Allow null values and return false/no cave? Yes!
        if (cavebiome == null) {
            return NO_CAVE;
        }
        Data data = noiseGens.get(cavebiome);
        if (data == null) {
            data = new Data();
            data.noiseGen = cavebiome.getNoiseGenCreator().apply(seed);
            NoiseGenerator noiseGen = data.noiseGen;
            data.scache = new SpecialCache2D<>(60, 55, (kx, ky) -> {
                return Util.smoothCA((x, y) -> noiseGen.get().get(x + 0.5, y + 0.5), kx * Chunk.CHUNK_SIZE,
                        ky * Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE, cavebiome.getSmoothRule(),
                        cavebiome.getMinSolidCount(), cavebiome.getIterations(), cavebiome.getThreshold());
            }, null);
            noiseGens.put(cavebiome, data);
        }
        int gtx = Chunk.toGlobalChunk(tx);
        int gty = Chunk.toGlobalChunk(ty);
        int[][] ints = data.scache.getOrFresh(gtx, gty);
        return ints[tx - gtx * Chunk.CHUNK_SIZE][ty - gty * Chunk.CHUNK_SIZE];
    }
    
    private float getCaveRawPercentage(int tx, int ty, int rad) {
        int count = 0;
        int total = 0;
        for (int iy = -rad; iy <= rad; iy++) {
            int dx = (int) Math.sqrt(rad * rad - iy * iy);
            for (int ix = -dx; ix <= dx; ix++) {
                total++;
                if (getCaveRaw(tx + ix, ty + iy) == CAVE) {
                    count++;
                }
            }
        }
        return count / (float) (total);
    }
    
    private float checkMonoBiome(int tx, int ty, int rad) {
        CaveBiome b = getCaveBiome(tx, ty);
        float near = Float.POSITIVE_INFINITY;
        for (int iy = -rad; iy <= rad; iy++) {
            int dx = (int) Math.sqrt(rad * rad - iy * iy);
            for (int ix = -dx; ix <= dx; ix++) {
                if (!Objects.equals(getCaveBiome(tx + ix, ty + iy), b)) {
                    float dist = (float) Math.sqrt(ix * ix + iy * iy);
                    if (dist < near) {
                        near = dist;
                    }
                }
            }
        }
        return near;
    }
}
