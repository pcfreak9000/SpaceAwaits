package mod;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.composer.ComposedTextureProvider;
import de.pcfreak9000.spaceawaits.composer.Composer;
import de.pcfreak9000.spaceawaits.content.items.Items;
import de.pcfreak9000.spaceawaits.content.tiles.Tiles;
import de.pcfreak9000.spaceawaits.core.CoreEvents;
import de.pcfreak9000.spaceawaits.core.TextureProvider;
import de.pcfreak9000.spaceawaits.crafting.CraftingManager;
import de.pcfreak9000.spaceawaits.crafting.ShapedRecipe;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.item.loot.GuaranteedInventoryContent;
import de.pcfreak9000.spaceawaits.item.loot.LootTable;
import de.pcfreak9000.spaceawaits.item.loot.WeightedRandomInventoryContent;
import de.pcfreak9000.spaceawaits.mod.Instance;
import de.pcfreak9000.spaceawaits.mod.Mod;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.util.Util;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldBounds;
import de.pcfreak9000.spaceawaits.world.WorldUtil;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.WorldGlobalComponent;
import de.pcfreak9000.spaceawaits.world.gen.GeneratorSettings;
import de.pcfreak9000.spaceawaits.world.gen.IPlayerSpawn;
import de.pcfreak9000.spaceawaits.world.gen.IWorldGenerator;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;
import de.pcfreak9000.spaceawaits.world.gen.WorldSetup;
import de.pcfreak9000.spaceawaits.world.gen.biome.BiomeChunkGenerator;
import de.pcfreak9000.spaceawaits.world.light.AmbientLightProvider;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.WorldView;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderFogComponent;
import de.pcfreak9000.spaceawaits.world.tile.ITileEntity;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.TileLiquid;
import layerteststuff.TestBiomeGenerator;

@Mod(id = "SpaceAwaits-Experimental", name = "Space Awaits Experimental Additions", version = { 0, 0, 2 })
public class DMod {
    
    @Instance
    public static DMod instance;
    TextureProvider texture = TextureProvider.get("sdfsdf");
    TextureProvider planet = TextureProvider.get("planet.png");
    public Tile laser = new Tile() {//FIXME laser does weird stuff? position and stuff
        @Override
        public boolean hasTileEntity() {
            return true;
        }
        
        @Override
        public ITileEntity createTileEntity(World world, int gtx, int gty, TileLayer layer) {
            return new LaserTileEntity(world, gtx, gty);
        };
    };
    public Tile torch = new Tile();
    
    public TileLiquid water = new TileLiquid();
    
    public SpaceshipFactory fac = new SpaceshipFactory();
    public static final Item MININGLASER = new ItemMininglaser();
    
    //TODO ALLGEMEIN:
    //Tile render modifikatoren -> tiles die besser mit nachbarn klarkommen und vlt noch mit großem gras und blumen over extenden oder andere texture??
    //random tick system, erledigt (mehr oder weniger) random tick system für entities
    //wasser + kleine hitboxen
    //terrain gen
    //STEINTYPEN!?? + textur?
    //baum animation, abreiß animation (-> klassen durchgucken die dinge nur bei gedrückt tun)
    @EventSubscription
    public void init(final CoreEvents.InitEvent init) {
        GameRegistry.WORLD_COMPONENT_REGISTRY.register("spaceawaitsComponentInventoryShip",
                ComponentInventoryShip.class);
        GameRegistry.WORLD_COMPONENT_REGISTRY.register("spaceawaitsCompositeInventoryComponent",
                CompositeInventoryComponent.class);
        
        GameRegistry.COMPOSITE_MANAGER.create(0, "Proton").build();
        GameRegistry.COMPOSITE_MANAGER.create(0, "Electron").build();
        GameRegistry.COMPOSITE_MANAGER.create(1, "Stonestuff").add(10f, "Proton").add(10f, "Electron").build();
        
        //        Animation<ITextureProvider> stoneanim = new Animation<>(5, TextureProvider.get("stone.png"),
        //                TextureProvider.get("sand.png"));
        GameRegistry.WORLD_ENTITY_REGISTRY.register("spac", fac);
        
        water.setTexture("stone.png");
        water.setSolid(false);
        water.setCanBreak(false);
        water.setLightTransmission(0.9f);
        water.setColor(new Color(0, 100 / 255f, 1, 0.1f));//0.75f
        water.setDisplayName("Water");
        water.setOpaque(false);
        GameRegistry.TILE_REGISTRY.register("water", water);
        
        Tile ironTile = new Tile();
        ironTile.setDisplayName("Iron Ore");
        ironTile.setTexture("ore_iron.png");
        // ironTile.setLightColor(new Color(Tile.MAX_LIGHT_VALUE, Tile.MAX_LIGHT_VALUE, Tile.MAX_LIGHT_VALUE));
        GameRegistry.TILE_REGISTRY.register("ore_iron", ironTile);
        
        torch.setLightColor(Color.WHITE);
        torch.setDisplayName("torch");
        GameRegistry.TILE_REGISTRY.register("torch", torch);
        //torch.setLightColor(new Color(Tile.MAX_LIGHT_VALUE, Tile.MAX_LIGHT_VALUE, Tile.MAX_LIGHT_VALUE));
        
        laser.setTexture("dirt.png");
        laser.setDisplayName("Laser");
        laser.setColor(Color.RED);
        laser.setLightColor(new Color(1, 0, 0, 1));
        GameRegistry.TILE_REGISTRY.register("laser", laser);
        
        Background back = new Background(new ComposedTextureProvider(
                new Composer(WorldView.VISIBLE_TILES_MAX * 40, WorldView.VISIBLE_TILES_MAX * 40) {
                    @Override
                    protected void render() {
                        super.render();
                        reee();
                    }
                }), WorldView.VISIBLE_TILES_MAX, WorldView.VISIBLE_TILES_MAX);
        GameRegistry.WORLD_ENTITY_REGISTRY.register("background.stars", back);
        Background b2 = new Background(planet, 5, 5);
        b2.xoff = -20;
        b2.yoff = 15;
        b2.w = 1;
        b2.h = 1;
        GameRegistry.WORLD_ENTITY_REGISTRY.register("background.planet", b2);
        //GameRegistry.WORLD_ENTITY_REGISTRY.register("fallingthing", new FallingEntityFactory());
        GameRegistry.ITEM_REGISTRY.register("mininglaser", MININGLASER);
        
    }
    
    @EventSubscription
    public void postinit(CoreEvents.PostInitEvent ev) {
        CraftingManager.instance().addSimpleRecipe(new ItemStack(Items.AXE_PRIMITIVE, 1), new ItemStack(Items.TWIG, 2),
                new ItemStack(Items.LOOSEROCK, 2));
        CraftingManager.instance().addSimpleRecipe(new ItemStack(Items.PICKAXE_PRIMITIVE, 1),
                new ItemStack(Items.TWIG, 2), new ItemStack(Items.LOOSEROCK, 3));
        CraftingManager.instance().addSimpleRecipe(new ItemStack(Tiles.WORKBENCH_PRIMITIVE, 1),
                new ItemStack(Tiles.WOOD, 4));
        CraftingManager.instance().addShapedRecipe(
                new ShapedRecipe(Items.AXE_PRIMITIVE, "XX", "SX", "S ", 'X', Items.LOOSEROCK, 'S', Items.TWIG));
        LootTable shipStarterTable = LootTable.getFor("shipspawn");
        //shipStarterTable.addMin(0);
        shipStarterTable.addMax(2);
        shipStarterTable.add(new GuaranteedInventoryContent(Items.REPAIRGUN, 1, 1));
        shipStarterTable.add(new GuaranteedInventoryContent(Items.MEDKIT_SIMPLE, 1, 2));
        shipStarterTable.add(new WeightedRandomInventoryContent(MININGLASER, 2, 1, 1, false));
        shipStarterTable.add(new WeightedRandomInventoryContent(torch.getItemTile(), 5, 2, 4, false));
        
        LootTable housethingTable = LootTable.getFor("housething");
        housethingTable.addMin(1);
        housethingTable.addMax(3);
        housethingTable.add(new WeightedRandomInventoryContent(MININGLASER, 10, 1, 1, false));
        housethingTable.add(new WeightedRandomInventoryContent(torch.getItemTile(), 100, 5, 10, false));
        housethingTable.add(new WeightedRandomInventoryContent(laser.getItemTile(), 3, 1, 2, false));
        GameRegistry.GENERATOR_REGISTRY.register("STS", new WorldSetup() {
            private static final int WIDTH = 5000;
            private static final int HEIGHT = 2500;
            
            @Override
            protected void initCaps() {
                this.CAPS.add(GeneratorCapabilitiesBase.LVL_ENTRY);
            }
            
            private Vector2 spawn = null;
            
            @Override
            public WorldPrimer setupWorld(GeneratorSettings genset) {
                WorldPrimer p = new WorldPrimer(this);
                p.setWorldGenerator(new IWorldGenerator() {
                    @Override
                    public void generate(World world) {
                        Entity ship = DMod.instance.fac.createEntity();
                        TransformComponent tc = ship.getComponent(TransformComponent.class);
                        Vector2 dim = ship.getComponent(PhysicsComponent.class).factory.boundingBoxWidthAndHeight();
                        Vector2 s = WorldUtil.findSpawnpoint(world, dim.x, dim.y, 0, 300, WIDTH, 700);
                        spawn = s;
                        tc.position.set(s);
                        //WorldUtil.simImpact(world.getSystem(TileSystem.class), s.x + 2, s.y + 4, 10, 0, 0, 0);
                        Components.STATS.get(ship).get("mechHealth").current = 1;
                        LootTable.getFor("shipspawn").generate(world.getWorldRandom(),
                                ship.getComponent(ComponentInventoryShip.class).invShip);
                        world.spawnEntity(ship, false);
                    }
                    
                    @Override
                    public void onLoading(World world) {
                        world.spawnEntity(GameRegistry.WORLD_ENTITY_REGISTRY.get("background.stars").createEntity(),
                                false);
                        world.spawnEntity(GameRegistry.WORLD_ENTITY_REGISTRY.get("background.planet").createEntity(),
                                false);
                        world.spawnEntity(testFogEntity(), false);
                    }
                });
                p.setPlayerSpawn(new IPlayerSpawn() {
                    
                    @Override
                    public Rectangle getSpawnArea(Player player) {
                        return new Rectangle(0, 300, WIDTH, 700);
                    }
                    
                    @Override
                    public Vector2 getPlayerSpawn(Player player, World world) {
                        Vector2 dim = player.getPlayerEntity().getComponent(PhysicsComponent.class).factory
                                .boundingBoxWidthAndHeight();
                        Rectangle rect = getSpawnArea(player);
                        return spawn;
                        //return WorldUtil.findSpawnpoint(world, dim.x, dim.y, rect.x, rect.y, rect.width, rect.height);
                    }
                });
                p.setWorldBounds(new WorldBounds(WIDTH, HEIGHT));
                p.setLightProvider(AmbientLightProvider.constant(Color.WHITE));
                p.setChunkGenerator(new BiomeChunkGenerator(new TestBiomeGenerator(genset.getSeed())));
                return p;
            }
        });
    }
    
    private Entity testFogEntity() {
        Entity e = new EntityImproved();
        RenderComponent rc = new RenderComponent(50);
        RenderFogComponent rfc = new RenderFogComponent();
        TransformComponent tc = new TransformComponent();
        tc.position.set(2497, 512);
        rfc.width = 2789 - 2497;
        rfc.height = 763 - 512;
        rfc.color = Color.CORAL;
        rfc.oversize = true;
        rfc.innerRect = new Rectangle(2607, 624, 2670 - 2607, 648 - 624);
        rfc.scalex = 60.0f;
        rfc.scaley = 10.0f;
        rfc.velx = 1;
        rfc.vely = 1;
        rfc.fbmVelx = 1;
        rfc.fbmVely = 1;
        rfc.coeffb = 0.5f;
        e.add(tc);
        e.add(rc);
        e.add(rfc);
        e.add(new WorldGlobalComponent());
        return e;
    }
    
    private void reee() {
        SpriteBatch b = new SpriteBatch();
        Camera cam = new OrthographicCamera(1, 1);
        b.setProjectionMatrix(cam.combined);
        //b.begin();
        ScreenUtils.clear(0, 0, 0, 0);
        ShapeRenderer s = new ShapeRenderer();
        s.setProjectionMatrix(cam.combined);
        s.begin(ShapeType.Filled);
        RandomXS128 r = new RandomXS128();
        for (int i = 0; i < 20000; i++) {
            float x = r.nextFloat();
            float y = r.nextFloat();
            Color c = Util.ofTemperature(20000 * r.nextFloat() + 800);
            c.mul(r.nextFloat() * 0.8f);
            c.a = 1;
            s.setColor(c);
            s.circle(x - 0.5f, y - 0.5f, 0.0006f * (0.75f + r.nextFloat()) / 4, 20);
            //b.draw(CoreRes.WHITE, x-0.5f, y-0.5f, 0.001f, 0.001f);
        }
        s.end();
        s.dispose();
        //b.end();
        b.dispose();
    }
}
