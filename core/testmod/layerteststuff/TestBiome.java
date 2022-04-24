package layerteststuff;

import java.util.Random;

import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleFractal.FractalType;
import com.sudoplay.joise.module.SeededModule;

import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.ITileArea;
import de.pcfreak9000.spaceawaits.world.gen.biome.Biome;
import de.pcfreak9000.spaceawaits.world.gen.biome.BiomeGenerator;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;
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
        source.setSampleScale(Chunk.CHUNK_SIZE * 2);
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
    public void genTerrainTileAt(int tx, int ty, ITileArea chunk, BiomeGenerator biomeGen, Random rand) {
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
            if (rand.nextDouble() < 0.001) {
                t = DMod.instance.laser;
            }
            if (rand.nextDouble() < 0.002) {
                t = DMod.instance.torch;
            }
        }
        chunk.setTile(tx, ty, TileLayer.Front, t);
        chunk.setTile(tx, ty, TileLayer.Back, t);
    }
    
    @Override
    public void populate(TileSystem tiles, World world, BiomeGenerator biomeGen, int tx, int ty, Random rand) {
        if (rand.nextDouble() <= 0.05) {
            int x = rand.nextInt(POPULATE_DIV) + tx;
            int y = rand.nextInt(POPULATE_DIV) + ty;
            tiles.setTile(x, y, TileLayer.Front, DMod.instance.torch);
        }
    }
    
}
