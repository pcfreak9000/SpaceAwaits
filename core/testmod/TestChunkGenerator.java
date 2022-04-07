import com.badlogic.ashley.core.Entity;
import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleFractal.FractalType;

import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.world.gen.IChunkGenerator;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class TestChunkGenerator implements IChunkGenerator {
    
    private Module genNoise(long seed) {
        ModuleFractal gen = new ModuleFractal(FractalType.FBM, BasisType.SIMPLEX, InterpolationType.LINEAR);
        gen.setSeed(seed);
        gen.setNumOctaves(6);
        gen.setFrequency(0.00184);
        gen.setLacunarity(2.1);
        
        ModuleAutoCorrect source = new ModuleAutoCorrect(-1, 1);
        source.setSource(gen);
        source.setSamples(10000);
        source.calculate2D();
        return gen;
    }
    
    private Module noise;
    
    public TestChunkGenerator(long seed) {
        this.noise = genNoise(seed);
    }
    
    @Override
    public void generateChunk(Chunk chunk, World world) {
        for (int i = 0; i < Chunk.CHUNK_SIZE; i++) {
            for (int j = 0; j < Chunk.CHUNK_SIZE; j++) {
                if (!world.getBounds().inBounds(i + chunk.getGlobalTileX(), j + chunk.getGlobalTileY())) {
                    continue;
                }
                int value = 400 + (int) Math.round(60 * noise.get(i + chunk.getGlobalTileX(), 0.5));
                //                int value = 400 + Mathf.round(6 * Mathf.abs(MathUtils.sin(0.2f * (i + chunk.getGlobalTileX())))
                //                        + 20 * Mathf.abs(MathUtils.sin(0.05f * (i + chunk.getGlobalTileX())))
                //                        + 40 * MathUtils.sin(0.02f * (i + chunk.getGlobalTileX()))
                //                        + 200 * MathUtils.sin(0.01f * (i + chunk.getGlobalTileX())));
                if (j + chunk.getGlobalTileY() > value) {
                    continue;
                }
                Tile t;
                if (j + chunk.getGlobalTileY() == 0) {
                    t = GameRegistry.TILE_REGISTRY.get("bottom");
                } else {
                    if (j + chunk.getGlobalTileY() == value) {
                        t = GameRegistry.TILE_REGISTRY.get("grass");
                    } else if (j + chunk.getGlobalTileY() >= value - 3) {
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
                chunk.setTile(i + chunk.getGlobalTileX(), j + chunk.getGlobalTileY(), TileLayer.Front, t);
                chunk.setTile(i + chunk.getGlobalTileX(), j + chunk.getGlobalTileY(), TileLayer.Back, t);
                if (Math.random() < 0.002) {
                    chunk.setTile(i + chunk.getGlobalTileX(), j + chunk.getGlobalTileY(), TileLayer.Front,
                            GameRegistry.TILE_REGISTRY.get("water"));
                }
            }
        }
        Entity ship = DMod.instance.fac.createEntity();
        TransformComponent tc = ship.getComponent(TransformComponent.class);//Not good?
        tc.position.set(chunk.getGlobalTileX(), chunk.getGlobalTileY() - 1);
        // chunk.addEntity(ship);
        //        Entity item = ItemEntityFactory.setupItemEntity(new ItemStack(DMod.instance.gun, 128), chunk.getGlobalTileX(),
        //                chunk.getGlobalTileY() - 1);
        //        chunk.addEntity(item);
        
        //        Entity entity = GameRegistry.WORLD_ENTITY_REGISTRY.get("fallingthing").createEntity();
        //        TransformComponent tc = entity.getComponent(TransformComponent.class);//Not good?
        //        tc.position.set(chunk.getGlobalTileX(), chunk.getGlobalTileY() - 1);
        //        chunk.addEntity(entity);
        
    }
}
