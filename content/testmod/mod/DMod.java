package mod;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.content.gen.SpaceSurfaceGenerator;
import de.pcfreak9000.spaceawaits.content.gen.SpaceSurfaceParams;
import de.pcfreak9000.spaceawaits.content.items.Items;
import de.pcfreak9000.spaceawaits.core.CoreEvents;
import de.pcfreak9000.spaceawaits.core.assets.InfiniteGeneratedTexture;
import de.pcfreak9000.spaceawaits.core.assets.SillouetteTexGen;
import de.pcfreak9000.spaceawaits.core.assets.StarfieldTexGen;
import de.pcfreak9000.spaceawaits.core.assets.TextureProvider;
import de.pcfreak9000.spaceawaits.core.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.core.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.generation.IGeneratingLayer;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.loot.GuaranteedInventoryContent;
import de.pcfreak9000.spaceawaits.item.loot.LootTable;
import de.pcfreak9000.spaceawaits.item.loot.WeightedRandomInventoryContent;
import de.pcfreak9000.spaceawaits.mod.Instance;
import de.pcfreak9000.spaceawaits.mod.Mod;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.world.ecs.WorldGlobalComponent;
import de.pcfreak9000.spaceawaits.world.gen.GeneratorSettings;
import de.pcfreak9000.spaceawaits.world.gen.HeightVariation;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderFogComponent;
import de.pcfreak9000.spaceawaits.world.tile.TileLiquid;

@Mod(id = "SpaceAwaits-Experimental", name = "Space Awaits Experimental Additions", version = { 0, 0, 2 })
public class DMod {
    
    @Instance
    public static DMod instance;
    TextureProvider texture = TextureProvider.get("sdfsdf");
    TextureProvider planet = TextureProvider.get("planet.png");
    //    public Tile laser = new Tile() {//FIXME laser does weird stuff? position and stuff
    //        @Override
    //        public boolean hasTileEntity() {
    //            return true;
    //        }
    //        
    //        @Override
    //        public ITileEntity createTileEntity(World world, int gtx, int gty, TileLayer layer) {
    //            return new LaserTileEntity(world, gtx, gty);
    //        };
    //    };
    
    public TileLiquid water = new TileLiquid();
    
    public SpaceshipFactory fac = new SpaceshipFactory();
    public static final Item MININGLASER = new ItemMininglaser();
    
    public TestTileRegen regen = new TestTileRegen();
    
    @EventSubscription
    public void init(final CoreEvents.InitEvent init) {
        Registry.COMPOSITE_MANAGER.create(0, "Proton").build();
        Registry.COMPOSITE_MANAGER.create(0, "Electron").build();
        Registry.COMPOSITE_MANAGER.create(1, "Stonestuff").add(10f, "Proton").add(10f, "Electron").build();
        
        Registry.REGEN_REGISTRY.register("testtile", regen);
        
        //        Animation<ITextureProvider> stoneanim = new Animation<>(5, TextureProvider.get("stone.png"),
        //                TextureProvider.get("sand.png"));
        GameRegistry.registerWorldEntity("spac", fac);
        
        water.setTexture("stone.png");
        water.setSolid(false);
        water.setCanBreak(false);
        water.setLightTransmission(0.9f);
        water.setColor(new Color(0, 100 / 255f, 1, 0.1f));//0.75f
        water.setDisplayName("Water");
        water.setOpaque(false);
        GameRegistry.registerTile("water", water);
        Background backbig = new Background(new InfiniteGeneratedTexture(1 / 32f, 1 / 32f, 2048, 2048, 20, 14,
                new StarfieldTexGen(2000, 32 * 0.0288f)));
        backbig.zdist = Float.POSITIVE_INFINITY;
        GameRegistry.registerWorldEntity("background.stars", backbig);
        Background b2 = new Background(planet, 15, 15);
        b2.layer = -990;
        b2.zdist = Float.POSITIVE_INFINITY;
        b2.x = -15;
        b2.y = 10;
        GameRegistry.registerWorldEntity("background.planet", b2);
        //GameRegistry.WORLD_ENTITY_REGISTRY.register("fallingthing", new FallingEntityFactory());
        GameRegistry.registerItem("mininglaser", MININGLASER);
        
        //That the heightvariation is based around yoff and not around 0 is a bit cumbersome and unintuitive
        Background mounts = new Background(new InfiniteGeneratedTexture(1 / 32f, 1 / 32f, 2048, 2048, 20, 14,
                new SillouetteTexGen(new HeightVariation(5 * 32, 1, 4 * 32))));
        mounts.layer = -980;
        mounts.zdist = 30000;
        mounts.yoff = 1000;
        GameRegistry.registerWorldEntity("background.mounts", mounts);
        
    }
    
    @EventSubscription
    public void postinit(CoreEvents.PostInitEvent ev) {
        LootTable shipStarterTable = LootTable.getFor("shipspawn");
        //shipStarterTable.addMin(0);
        shipStarterTable.addMax(2);
        //shipStarterTable.add(new GuaranteedInventoryContent(Items.REPAIRGUN, 1, 1));
        shipStarterTable.add(new GuaranteedInventoryContent(Items.MEDKIT_SIMPLE, 1, 2));
        shipStarterTable.add(new WeightedRandomInventoryContent(MININGLASER, 2, 1, 1, false));
        //        Texture t = Util.combine(TextureProvider.EMPTY, TextureProvider.get("ironIngot.png"));
        //        TextureRegion tr = new TextureRegion(t);
        //        ITextureProvider tp = new ITextureProvider() {
        //            
        //            @Override
        //            public TextureRegion getRegion() {
        //                return tr;
        //            }
        //        };
        //        Background back = new Background(tp, WorldScreen.VISIBLE_TILES_MAX/40, WorldScreen.VISIBLE_TILES_MAX/40);
        //        GameRegistry.registerWorldEntity("background.stars", back);
        LootTable housethingTable = LootTable.getFor("housething");
        housethingTable.addMin(1);
        housethingTable.addMax(3);
        housethingTable.add(new WeightedRandomInventoryContent(MININGLASER, 10, 1, 1, false));
        //housethingTable.add(new WeightedRandomInventoryContent(laser.getItemTile(), 3, 1, 2, false));
        SpaceSurfaceGenerator gen = new SpaceSurfaceGenerator();
        Registry.GENERATOR_REGISTRY.registerWorldGen("STS", new IGeneratingLayer<WorldPrimer, GeneratorSettings>() {
            
            @Override
            public WorldPrimer generate(GeneratorSettings params) {
                return gen.generate(new SpaceSurfaceParams(params.getSeed(), 5000, 2500));
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
    
}
