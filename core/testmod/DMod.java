
import com.badlogic.gdx.graphics.Color;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.CoreEvents;
import de.pcfreak9000.spaceawaits.core.CoreResources;
import de.pcfreak9000.spaceawaits.core.TextureProvider;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.mod.Instance;
import de.pcfreak9000.spaceawaits.mod.Mod;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.serialize.SerializableEntityList;
import de.pcfreak9000.spaceawaits.world.Background;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldBounds;
import de.pcfreak9000.spaceawaits.world.WorldPrimer;
import de.pcfreak9000.spaceawaits.world.WorldUtil;
import de.pcfreak9000.spaceawaits.world.gen.IUnchunkGenerator;
import de.pcfreak9000.spaceawaits.world.gen.WorldGenerator;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.TileEntity;

@Mod(id = "SpaceAwaits-Dummy-Mod", name = "Kek", version = { 0, 0, 1 })
public class DMod {
    
    @Instance
    public static DMod instance;
    
    TextureProvider texture = TextureProvider.get("sdfsdf");
    Tile tstoneTile = new Tile();
    Tile laser = new Tile() {
        @Override
        public boolean hasTileEntity() {
            return true;
        }
        
        @Override
        public TileEntity createTileEntity(World world, int gtx, int gty) {
            return new LaserTileEntity(world, gtx, gty);
        };
    };
    Tile torch = new Tile();
    Item gun = new ItemGun();
    
    @EventSubscription
    public void init(final CoreEvents.InitEvent init) {
        //        Animation<ITextureProvider> stoneanim = new Animation<>(5, TextureProvider.get("stone.png"),
        //                TextureProvider.get("sand.png"));
        
        gun.setTexture("gun_0.png");
        GameRegistry.ITEM_REGISTRY.register("gun", gun);
        
        tstoneTile.setTexture("stone.png");
        GameRegistry.TILE_REGISTRY.register("stone", tstoneTile);
        tstoneTile.setLightTransmission(0.55f);
        
        Tile ironTile = new Tile();
        ironTile.setTexture("ore_iron.png");
        // ironTile.setLightColor(new Color(Tile.MAX_LIGHT_VALUE, Tile.MAX_LIGHT_VALUE, Tile.MAX_LIGHT_VALUE));
        GameRegistry.TILE_REGISTRY.register("ore_iron", ironTile);
        
        Tile bottom = new Tile();
        bottom.setCanBreak(false);
        bottom.setTexture("stone_dark.png");
        GameRegistry.TILE_REGISTRY.register("bottom", bottom);
        
        Tile grasstile = new Tile();
        grasstile.setTexture("grass.png");
        //grasstile.setFilterColor(new Color(0.3f, 0.3f, 0.3f));
        GameRegistry.TILE_REGISTRY.register("grass", grasstile);
        
        Tile dirttile = new Tile();
        dirttile.setTexture("dirt.png");
        dirttile.setBouncyness(1);
        //dirttile.setFilterColor(new Color(1, 0, 0, 1));
        GameRegistry.TILE_REGISTRY.register("dirt", dirttile);
        
        torch.setLightColor(new Color(0, 1, 0, 1));
        GameRegistry.TILE_REGISTRY.register("torch", torch);
        //torch.setLightColor(new Color(Tile.MAX_LIGHT_VALUE, Tile.MAX_LIGHT_VALUE, Tile.MAX_LIGHT_VALUE));
        
        laser.setTexture("dirt.png");
        laser.color().set(1, 0, 0, 1);
        laser.setLightColor(new Color(1, 0, 0, 1));
        GameRegistry.TILE_REGISTRY.register("laser", laser);
        
        Background back = new Background(CoreResources.SPACE_BACKGROUND, 1920 / 16 * 16f / 9, 1920 / 16);
        GameRegistry.WORLD_ENTITY_REGISTRY.register("background.stars", back);
        
        GameRegistry.WORLD_ENTITY_REGISTRY.register("fallingthing", new FallingEntityFactory());
        
        GameRegistry.GENERATOR_REGISTRY.register("STS", new WorldGenerator() {
            private static final int WIDTH = 5000;
            private static final int HEIGHT = 2500;
            
            @Override
            protected void initCaps() {
                this.CAPS.add(GeneratorCapabilitiesBase.LVL_ENTRY);
            }
            
            @Override
            public WorldPrimer generateWorld(long seed) {
                WorldPrimer p = new WorldPrimer();
                p.setWorldBounds(new WorldBounds(WIDTH, HEIGHT));
                p.setChunkGenerator(new TestChunkGenerator());
                p.setUnchunkGenerator(new IUnchunkGenerator() {
                    
                    @Override
                    public void generateUnchunk(SerializableEntityList entities, World world) {
                        entities.addEntity(GameRegistry.WORLD_ENTITY_REGISTRY.get("background.stars").createEntity());
                        WorldUtil.createWorldBorders(entities.getEntities(), WIDTH, HEIGHT);
                    }
                    
                    @Override
                    public void regenerateUnchunk(SerializableEntityList entities, World world) {
                        entities.addEntity(GameRegistry.WORLD_ENTITY_REGISTRY.get("background.stars").createEntity());
                        WorldUtil.createWorldBorders(entities.getEntities(), WIDTH, HEIGHT);
                    }
                });
                return p;
            }
        });
    }
}
