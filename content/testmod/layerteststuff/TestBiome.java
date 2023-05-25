package layerteststuff;

import java.util.Random;

import de.pcfreak9000.spaceawaits.content.tiles.TileEntityStorageDrawer;
import de.pcfreak9000.spaceawaits.content.tiles.Tiles;
import de.pcfreak9000.spaceawaits.generation.GenerationParameters;
import de.pcfreak9000.spaceawaits.generation.RndHelper;
import de.pcfreak9000.spaceawaits.item.loot.LootTable;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.ITileArea;
import de.pcfreak9000.spaceawaits.world.gen.ShapeSystem;
import de.pcfreak9000.spaceawaits.world.gen.biome.Biome;
import de.pcfreak9000.spaceawaits.world.gen.biome.Decorator;
import de.pcfreak9000.spaceawaits.world.gen.biome.SurfaceDecorator;
import de.pcfreak9000.spaceawaits.world.gen.feature.FeatureGenData;
import de.pcfreak9000.spaceawaits.world.gen.feature.IFeature;
import de.pcfreak9000.spaceawaits.world.gen.feature.ITilePlacer;
import de.pcfreak9000.spaceawaits.world.gen.feature.OreGenTileFeature;
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
        this.addTag(sub ? "lower" : "higher");
        this.tile = sub ? Tiles.STONE_DARK : Tiles.STONE;
        this.topTile = sub ? Tiles.STONE_DARK : Tiles.GRASS;
        this.surfaceDeco = sub ? null : new SurfaceDecorator();
        this.deco = new Decorator();
        if (!sub) {
            surfaceDeco.addFeature(new FeatureGenData(0.01f, fgen, null));
            surfaceDeco.addFeature(new FeatureGenData(0.1f, new TreeFeature(),
                    (tiles, b, param, x, y) -> tiles.getTile(x, y, TileLayer.Front) == Tiles.GRASS));
            surfaceDeco.addFeature(new FeatureGenData(0.1f, (world, tiles, x, y, rand) -> {
                tiles.setTile(x, y + 1, TileLayer.Front, Tiles.LOOSEROCKS);
                return true;
            }, (tiles, b, param, x, y) -> tiles.getTile(x, y, TileLayer.Front) == Tiles.GRASS));
        }
        this.deco.addFeature(new FeatureGenData(0.003f, coal,
                (a, b, params, x, y) -> params.getComponent(ShapeSystem.class).getHeight(x) - y > 5));
        this.deco.addFeature(new FeatureGenData(0.003f, copper,
                (a, b, params, x, y) -> params.getComponent(ShapeSystem.class).getHeight(x) - y > 19));
        this.deco.addFeature(new FeatureGenData(0.003f, iron,
                (a, b, params, x, y) -> params.getComponent(ShapeSystem.class).getHeight(x) - y > 12));
    }
    
    @Override
    public Tile genTileAt(int tx, int ty, TileLayer layer, GenerationParameters biomeGen, RndHelper rnd) {
        int value = biomeGen.getComponent(ShapeSystem.class).getHeight(tx);
        
        Tile t = Tile.NOTHING;
        if (ty == 0) {
            t = Tiles.BEDROCK;//<----
        } else {
            if (sub) {
                return Tiles.STONE_DARK;
            }
            if (ty == value) {
                t = Tiles.GRASS;
            } else if (ty >= value - 3) {
                t = Tiles.DIRT;//<----
            } else {
                t = Tiles.STONE;
            }
        }
        return t;
    }
    
    private IFeature fgen = new IFeature() {
        
        @Override
        public boolean generate(World world, ITileArea tiles, int tx, int ty, Random rand) {
            bp.generate((TileSystem) tiles, world, tx, ty, 0, 0, bp.getWidth(), bp.getHeight(), rand);
            return true;
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
    
    private OreGenTileFeature iron = new OreGenTileFeature(Tiles.ORE_IRON, 6, 9);
    private OreGenTileFeature copper = new OreGenTileFeature(Tiles.ORE_COPPER, 8, 12);
    private OreGenTileFeature coal = new OreGenTileFeature(Tiles.ORE_COAL, 6, 10);
    
    @Override
    public void populate(TileSystem tiles, World world, GenerationParameters biomeGen, int tx, int ty, int area,
            RndHelper rnd) {
        
    }
    
    @Override
    public void genStructureTiles(TileSystem tiles, GenerationParameters biomeGen, int tx, int ty, int area,
            RndHelper rnd) {
        //FIXME tiles is null...
    }
    
}
