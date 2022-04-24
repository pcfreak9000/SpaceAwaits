package layerteststuff;

import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleFractal.FractalType;
import com.sudoplay.joise.module.SeededModule;

import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.gen.biome.Biome;
import de.pcfreak9000.spaceawaits.world.gen.biome.BiomeGenerator;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import mod.DMod;

public class TestBiome extends Biome implements HeightSupplier {
    private void genNoise() {
        ModuleFractal gen = new ModuleFractal(FractalType.FBM, BasisType.SIMPLEX, InterpolationType.LINEAR);
        gen.setNumOctaves(6);
        gen.setFrequency(0.00184);
        gen.setLacunarity(2.1);
        seeded = gen;
        
        ModuleAutoCorrect source = new ModuleAutoCorrect(-1, 1);
        source.setSource(gen);
        source.setSamples(10000);
        source.calculate2D();
        noise = source;
    }
    
    private Module noise;
    private SeededModule seeded;
    
    public TestBiome() {
        genNoise();
        this.interpolators.put(HeightInterpolatable.class, new HeightInterpolatable(this));
    }
    
    @Override
    public float getHeight(int tx, int ty) {
        return 400 + (int) Math.round(60 * noise.get(tx, 0.5));
    }
    
    private long seedCurrent = SeededModule.DEFAULT_SEED;
    
    private void checkSetSeed(long seed) {
        if (seedCurrent != seed) {
            seeded.setSeed(seed);
            seedCurrent = seed;
        }
    }
    
    @Override
    public void genTerrainTileAt(int tx, int ty, Chunk chunk, BiomeGenerator biomeGen) {
        checkSetSeed(biomeGen.getWorldSeed());
        int value = (int) biomeGen.interpolateAlongX(HeightInterpolatable.class, tx, ty);
        if (ty > value) {
            return;
        }
        Tile t;
        if (ty == 0) {
            t = GameRegistry.TILE_REGISTRY.get("bottom");
        } else {
            if (ty == value) {
                t = GameRegistry.TILE_REGISTRY.get("grass");
            } else if (ty >= value - 3) {
                t = GameRegistry.TILE_REGISTRY.get("dirt");
            } else {
                t = GameRegistry.TILE_REGISTRY.get("stone");
            }
        }
        
        if (t == DMod.instance.tstoneTile) {
            if (Math.random() < 0.001) {
                t = DMod.instance.laser;
            }
            if (Math.random() < 0.002) {
                t = DMod.instance.torch;
            }
        }
        chunk.setTile(tx, ty, TileLayer.Front, t);
        chunk.setTile(tx, ty, TileLayer.Back, t);
        if (Math.random() < 0.002) {
            chunk.setTile(tx, ty, TileLayer.Front, GameRegistry.TILE_REGISTRY.get("water"));
        }
    }
    
    @Override
    public void decorate(Chunk chunk) {
    }
    
}
