
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.composer.ComposedTextureProvider;
import de.pcfreak9000.spaceawaits.composer.Composer;
import de.pcfreak9000.spaceawaits.core.CoreEvents;
import de.pcfreak9000.spaceawaits.core.TextureProvider;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.mod.Instance;
import de.pcfreak9000.spaceawaits.mod.Mod;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.serialize.SerializableEntityList;
import de.pcfreak9000.spaceawaits.util.Util;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldBounds;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.gen.IUnchunkGenerator;
import de.pcfreak9000.spaceawaits.world.gen.WorldGenerator;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;
import de.pcfreak9000.spaceawaits.world.light.AmbientLightProvider;
import de.pcfreak9000.spaceawaits.world.render.WorldView;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderFogComponent;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.TileEntity;
import de.pcfreak9000.spaceawaits.world.tile.TileLiquid;

@Mod(id = "SpaceAwaits-Dummy-Mod", name = "Kek", version = { 0, 0, 2 })
public class DMod {
    
    @Instance
    public static DMod instance;
    TextureProvider texture = TextureProvider.get("sdfsdf");
    TextureProvider planet = TextureProvider.get("planet.png");
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
    
    TileLiquid water = new TileLiquid();
    
    @EventSubscription
    public void init(final CoreEvents.InitEvent init) {
        //        Animation<ITextureProvider> stoneanim = new Animation<>(5, TextureProvider.get("stone.png"),
        //                TextureProvider.get("sand.png"));
        
        water.setTexture("stone.png");
        water.setSolid(false);
        water.setCanBreak(false);
        water.setCanBeReplaced(true);
        water.setLightTransmission(0.9f);
        water.color().set(0, 100 / 255f, 1, 0.1f);//0.75f
        water.setDisplayName("Water");
        water.setOpaque(false);
        GameRegistry.TILE_REGISTRY.register("water", water);
        
        gun.setTexture("gun_0.png");
        GameRegistry.ITEM_REGISTRY.register("gun", gun);
        
        tstoneTile.setTexture("stone.png");
        tstoneTile.setDisplayName("Stone");
        GameRegistry.TILE_REGISTRY.register("stone", tstoneTile);
        tstoneTile.setLightTransmission(0.6f);
        
        Tile ironTile = new Tile();
        ironTile.setDisplayName("Iron Ore");
        ironTile.setTexture("ore_iron.png");
        // ironTile.setLightColor(new Color(Tile.MAX_LIGHT_VALUE, Tile.MAX_LIGHT_VALUE, Tile.MAX_LIGHT_VALUE));
        GameRegistry.TILE_REGISTRY.register("ore_iron", ironTile);
        
        Tile bottom = new Tile();
        bottom.setCanBreak(false);
        bottom.setTexture("stone_dark.png");
        bottom.setDisplayName("Bedrock or something");
        GameRegistry.TILE_REGISTRY.register("bottom", bottom);
        
        Tile grasstile = new Tile();
        grasstile.setDisplayName("Grass tile");
        grasstile.setTexture("grass.png");
        //grasstile.setFilterColor(new Color(0.3f, 0.3f, 0.3f));
        GameRegistry.TILE_REGISTRY.register("grass", grasstile);
        
        Tile dirttile = new Tile();
        dirttile.setDisplayName("Dirt");
        dirttile.setTexture("dirt.png");
        //dirttile.setBouncyness(1);
        //dirttile.setFilterColor(new Color(1, 0, 0, 1));
        GameRegistry.TILE_REGISTRY.register("dirt", dirttile);
        
        torch.setLightColor(Color.WHITE);
        torch.setDisplayName("torch");
        GameRegistry.TILE_REGISTRY.register("torch", torch);
        //torch.setLightColor(new Color(Tile.MAX_LIGHT_VALUE, Tile.MAX_LIGHT_VALUE, Tile.MAX_LIGHT_VALUE));
        
        laser.setTexture("dirt.png");
        laser.setDisplayName("Laser");
        laser.color().set(1, 0, 0, 1);
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
        
        GameRegistry.GENERATOR_REGISTRY.register("STS", new WorldGenerator() {
            private static final int WIDTH = 5000;
            private static final int HEIGHT = 2500;
            
            @Override
            protected void initCaps() {
                this.CAPS.add(GeneratorCapabilitiesBase.LVL_ENTRY);
            }
            
            @Override
            public WorldPrimer generateWorld(long seed) {
                WorldPrimer p = new WorldPrimer(this);
                p.setPlayerSpawn((pl) -> new Rectangle(0, 300, WIDTH, 700));
                p.setWorldBounds(new WorldBounds(WIDTH, HEIGHT));
                p.setLightProvider(AmbientLightProvider.constant(Color.WHITE));
                p.setChunkGenerator(new TestChunkGenerator());
                p.setUnchunkGenerator(new IUnchunkGenerator() {
                    
                    @Override
                    public void generateUnchunk(SerializableEntityList entities, World world) {
                        entities.addEntity(GameRegistry.WORLD_ENTITY_REGISTRY.get("background.stars").createEntity());
                        entities.addEntity(b2.createEntity());
                        entities.addEntity(testFogEntity());
                    }
                    
                    @Override
                    public void regenerateUnchunk(SerializableEntityList entities, World world) {
                        entities.addEntity(GameRegistry.WORLD_ENTITY_REGISTRY.get("background.stars").createEntity());
                        entities.addEntity(b2.createEntity());
                        entities.addEntity(testFogEntity());
                    }
                });
                //                p.setLightProvider(new AmbientLightProvider() {
                //                    
                //                    @Override
                //                    public Color getAmbientLightNew(int tx, int ty) {
                //                        return Util.ofTemperature(
                //                                SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().getWorldCurrent().time);
                //                    }
                //                });
                return p;
            }
        });
    }
    
    //    private Entity fog() {
    //        Entity e = new EntityImproved();
    //        e.add(new RenderComponent(10, "entity"));
    //        RenderTextureComponent rtc = new RenderTextureComponent();
    //        rtc.color = Color.CYAN;
    //        rtc.texture = fog;
    //        rtc.width = 1000;
    //        rtc.height = 200;
    //        e.add(rtc);
    //        TransformComponent tc = new TransformComponent();
    //        tc.position.set(2000, 500);
    //        e.add(tc);
    //        return e;
    //    }
    
    private Entity testFogEntity() {
        Entity e = new EntityImproved();
        RenderComponent rc = new RenderComponent(50, "fog");
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
