package layerteststuff;

import java.util.Random;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.content.entities.Entities;
import de.pcfreak9000.spaceawaits.content.gen.HeightComponent;
import de.pcfreak9000.spaceawaits.content.items.Items;
import de.pcfreak9000.spaceawaits.content.tiles.TileEntityStorageDrawer;
import de.pcfreak9000.spaceawaits.content.tiles.Tiles;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.item.loot.LootTable;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.ITileArea;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.EntityInteractSystem;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.world.gen.RndHelper;
import de.pcfreak9000.spaceawaits.world.gen.biome.Biome;
import de.pcfreak9000.spaceawaits.world.gen.biome.BiomeGenCompBased;
import de.pcfreak9000.spaceawaits.world.gen.feature.FeatureGenerator;
import de.pcfreak9000.spaceawaits.world.gen.feature.ITilePlacer;
import de.pcfreak9000.spaceawaits.world.gen.feature.StringBasedBlueprint;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class TestBiome extends Biome {
    
    private StringBasedBlueprint bp;
    
    private static final String[] leet = { // 
            "#$###$###$###", //
            "#$$$#$$$#$$$#", //
            "#$###$#X#$$$#", //
            "#$$$#$$$#$$$#", //
            "#$###$###$$$#", //
    };
    
    private boolean sub;
    
    public TestBiome(boolean sub) {
        this.sub = sub;
        this.bp = new StringBasedBlueprint();
        this.bp.setFront(leet, '#', Tiles.BRICKS_OLD, 'X', storageDrawer);
        this.bp.setBack(leet, '#', Tiles.BRICKS_OLD, 'X', Tiles.BRICKS_OLD);
    }
    
    @Override
    public void genTerrainTileAt(int tx, int ty, ITileArea chunk, BiomeGenCompBased biomeGen, RndHelper rnd) {
        if (sub) {
            chunk.setTile(tx, ty, TileLayer.Front, Tiles.STONE_DARK);
            chunk.setTile(tx, ty, TileLayer.Back, Tiles.STONE_DARK);
            return;
        }
        int value = biomeGen.getComponent(HeightComponent.class).getHeight(tx, ty);
        if (ty > value + 1) {
            return;
        }
        Tile t;
        if (ty == 0) {
            t = Tiles.BEDROCK;
        } else {
            if (ty == value + 1) {
                if (rnd.getRandom().nextDouble() < 0.1) {
                    t = Tiles.LOOSEROCKS;
                } else {
                    return;
                }
            } else if (ty == value) {
                t = Tiles.GRASS;
            } else if (ty >= value - 3) {
                t = Tiles.DIRT;
            } else {
                t = Tiles.STONE;
            }
        }
        chunk.setTile(tx, ty, TileLayer.Front, t);
        if (t != Tiles.LOOSEROCKS) {
            chunk.setTile(tx, ty, TileLayer.Back, t);
        }
    }
    
    private FeatureGenerator fgen = new FeatureGenerator() {
        
        @Override
        public boolean generate(TileSystem tiles, World world, int tx, int ty, Random rand, int area) {
            if (tiles.getTile(tx, ty, TileLayer.Front) == Tiles.BRICKS_OLD) {
                tiles.setTile(tx, ty + 1, TileLayer.Front, Tiles.BRICKS_OLD);
                tiles.setTile(tx, ty + 2, TileLayer.Front, Tiles.BRICKS_OLD);
                tiles.setTile(tx, ty + 3, TileLayer.Front, Tiles.BRICKS_OLD);
                tiles.setTile(tx - 1, ty + 3, TileLayer.Front, Tiles.BRICKS_OLD);
                tiles.setTile(tx + 1, ty + 3, TileLayer.Front, Tiles.BRICKS_OLD);
                return true;
            }
            return false;
        }
    };
    
    private ITilePlacer storageDrawer = new ITilePlacer() {
        
        @Override
        public void place(int tx, int ty, TileLayer layer, Random random, ITileArea tiles) {
            tiles.setTile(tx, ty, layer, Tiles.STORAGE_DRAWER);
            TileEntityStorageDrawer te = (TileEntityStorageDrawer) tiles.getTileEntity(tx, ty, layer);
            LootTable.getFor("housething").generate(random, te);
        }
    };
    
    @Override
    public void populate(TileSystem tiles, World world, BiomeGenCompBased biomeGen, int tx, int ty, int area,
            RndHelper rnd) {
        if (sub)
            return;
        for (int i = 0; i < 4; i++) {
            int x = rnd.getRandom().nextInt(area) + tx;
            int y = rnd.getRandom().nextInt(area) + ty;
            if (tiles.getTile(x, y, TileLayer.Front) == Tiles.STONE) {
                for (int j = -2; j <= 2; j++) {
                    for (int k = -2; k <= 2; k++) {
                        if (j * j + k * k < 2 * 2) {
                            if (tiles.getTile(x + j, y + k, TileLayer.Front) == Tiles.STONE) {
                                tiles.setTile(x + j, y + k, TileLayer.Front, Tiles.ORE_POOR_IRON);
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < 6; i++) {
            int x = rnd.getRandom().nextInt(area) + tx;
            int y = rnd.getRandom().nextInt(area) + ty;
            if (tiles.getTile(x, y, TileLayer.Front) == Tiles.STONE) {
                for (int j = -2; j <= 2; j++) {
                    for (int k = -2; k <= 2; k++) {
                        if (j * j + k * k < 2 * 2) {
                            if (tiles.getTile(x + j, y + k, TileLayer.Front) == Tiles.STONE) {
                                tiles.setTile(x + j, y + k, TileLayer.Front, Tiles.ORE_COAL);
                            }
                        }
                    }
                }
            }
        }
        //this algorithm is f*cking slow
        for (int i = 0; i < 50; i++) {
            int x = rnd.getRandom().nextInt(area) + tx;
            int y = rnd.getRandom().nextInt(area) + ty;
            if (tiles.getTile(x, y, TileLayer.Front) == Tiles.GRASS) {
                //tiles.setTile(x, y, TileLayer.Front, DMod.instance.torch);
                //fgen.generate(tiles, world, x, y, rand, area);
                //this.bp.generate(tiles, world, x, y, 0, 0, bp.getWidth(), bp.getHeight(), rand);
                Entity tree = Entities.TREE.createEntity();
                TransformComponent tc = Components.TRANSFORM.get(tree);
                tc.position.set(x - 0.5f, y + 1);
                world.getSystem(EntityInteractSystem.class).spawnEntity(tree, false);
                if (rnd.getRandom().nextDouble() < 0.3) {
                    ItemStack s = new ItemStack(Items.TWIG, rnd.getRandom().nextInt(1) + 1);
                    s.dropRandomInTile(world, x, y + 1);
                }
            }
        }
    }
    
    @Override
    public void genStructureTiles(TileSystem tiles, BiomeGenCompBased biomeGen, int tx, int ty, int area,
            RndHelper rnd) {
        //FIXME tiles is null...
    }
    
}
