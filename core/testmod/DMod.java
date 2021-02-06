
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.CoreEvents;
import de.pcfreak9000.spaceawaits.core.TextureProvider;
import de.pcfreak9000.spaceawaits.mod.Instance;
import de.pcfreak9000.spaceawaits.mod.Mod;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.tileworld.Background;
import de.pcfreak9000.spaceawaits.tileworld.WorldAccessor;
import de.pcfreak9000.spaceawaits.tileworld.WorldGenerator;
import de.pcfreak9000.spaceawaits.tileworld.ecs.PhysicsComponent;
import de.pcfreak9000.spaceawaits.tileworld.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.tileworld.ecs.entity.MovingWorldEntityComponent;
import de.pcfreak9000.spaceawaits.tileworld.ecs.entity.RenderEntityComponent;
import de.pcfreak9000.spaceawaits.tileworld.ecs.entity.TextureSpriteAction;
import de.pcfreak9000.spaceawaits.tileworld.light.AmbientLightProvider;
import de.pcfreak9000.spaceawaits.tileworld.tile.Chunk;
import de.pcfreak9000.spaceawaits.tileworld.tile.TestWorldProvider;
import de.pcfreak9000.spaceawaits.tileworld.tile.Tile;
import de.pcfreak9000.spaceawaits.tileworld.tile.TileEntity;
import de.pcfreak9000.spaceawaits.tileworld.tile.TileState;
import de.pcfreak9000.spaceawaits.tileworld.tile.WorldProvider;

@Mod(id = "SpaceAwaits-Dummy-Mod", name = "Kek", version = { 0, 0, 1 })
public class DMod {
    
    @Instance
    private static DMod instance;
    
    @EventSubscription
    public void init(final CoreEvents.InitEvent init) {
        Tile tstoneTile = new Tile();
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
        
        Tile torch = new Tile();
        GameRegistry.TILE_REGISTRY.register("torch", torch);
        //torch.setLightColor(new Color(Tile.MAX_LIGHT_VALUE, Tile.MAX_LIGHT_VALUE, Tile.MAX_LIGHT_VALUE));
        
        Tile laser = new Tile() {
            @Override
            public boolean hasTileEntity() {
                return true;
            }
            
            @Override
            public TileEntity createTileEntity(WorldAccessor world, TileState myState) {
                return new LaserTileEntity(world, myState);
            }
        };
        laser.setTexture("dirt.png");
        laser.color().set(1, 0, 0, 1);
        laser.setLightColor(new Color(1, 0, 0, 1));
        GameRegistry.TILE_REGISTRY.register("laser", laser);
        
        Background back = new Background("Space.png", 1920 * 16f / 9, 1920);
        GameRegistry.BACKGROUND_REGISTRY.register("stars", back);
        
        TextureProvider texture = new TextureProvider("sdfsdf");
        
        GameRegistry.GENERATOR_REGISTRY.register("STS", new WorldGenerator() {
            
            @Override
            protected void initCaps() {
                this.CAPS.add(GeneratorCapabilitiesBase.LVL_ENTRY);
            }
            
            @Override
            public WorldProvider generateWorld(long seed) {
                return new TestWorldProvider(400, 400, (chunk, tileWorld) -> {
                    for (int i = 0; i < Chunk.CHUNK_TILE_SIZE; i++) {
                        for (int j = 0; j < Chunk.CHUNK_TILE_SIZE; j++) {
                            if (!tileWorld.getMeta().inBounds(i + chunk.getGlobalTileX(), j + chunk.getGlobalTileY())) {
                                continue;
                            }
                            int value = 75
                                    + Mathf.round(6 * Mathf.abs(MathUtils.sin(0.2f * (i + chunk.getGlobalTileX())))
                                            + 20 * Mathf.abs(MathUtils.sin(0.05f * (i + chunk.getGlobalTileX()))));
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
                            
                            if (t == tstoneTile) {
                                if (Math.random() < 0.001) {
                                    t = laser;
                                }
                                if (Math.random() < 0.002) {
                                    t = torch;
                                }
                            }
                            chunk.setTile(t, i + chunk.getGlobalTileX(), j + chunk.getGlobalTileY());
                            chunk.setTileBackground(t, i + chunk.getGlobalTileX(), j + chunk.getGlobalTileY());
                        }
                    }
                    Entity entity = new Entity();
                    entity.add(new MovingWorldEntityComponent());
                    RenderEntityComponent rec = new RenderEntityComponent();
                    Sprite s = new Sprite();
                    s.setSize(200, 100);
                    rec.sprite = s;
                    rec.action = new TextureSpriteAction(texture);
                    entity.add(rec);
                    TransformComponent tc = new TransformComponent();
                    entity.add(tc);
                    tc.position.set(chunk.getGlobalTileX() * Tile.TILE_SIZE,
                            tileWorld.getMeta().getHeight() * Tile.TILE_SIZE-1);
                    chunk.addEntity(entity);
                    PhysicsComponent pc = new PhysicsComponent();
                    pc.acceleration.set(0, -98.1f);
                    pc.w = 200;
                    pc.h = 100;
                    entity.add(pc);
                    //chunk.requestSunlightComputation();
                }, GameRegistry.BACKGROUND_REGISTRY.get("stars"), AmbientLightProvider.constant(Color.WHITE));
            }
        });
    }
}
