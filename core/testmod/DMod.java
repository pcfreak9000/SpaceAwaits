
import com.badlogic.gdx.graphics.Color;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.CoreEvents;
import de.pcfreak9000.spaceawaits.core.TextureProvider;
import de.pcfreak9000.spaceawaits.mod.Instance;
import de.pcfreak9000.spaceawaits.mod.Mod;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.world.Background;
import de.pcfreak9000.spaceawaits.world.WorldAccessor;
import de.pcfreak9000.spaceawaits.world.WorldMeta;
import de.pcfreak9000.spaceawaits.world.gen.WorldGenerationBundle;
import de.pcfreak9000.spaceawaits.world.gen.WorldGenerator;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.TileEntity;
import de.pcfreak9000.spaceawaits.world.tile.TileState;

@Mod(id = "SpaceAwaits-Dummy-Mod", name = "Kek", version = { 0, 0, 1 })
public class DMod {
    
    @Instance
    public static DMod instance;
    
    TextureProvider texture = new TextureProvider("sdfsdf");
    Tile tstoneTile = new Tile();
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
    Tile torch = new Tile();
    
    @EventSubscription
    public void init(final CoreEvents.InitEvent init) {
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
        
        Background back = new Background("Space.png", 1920 * 16f / 9, 1920);
        GameRegistry.BACKGROUND_REGISTRY.register("stars", back);
        
        GameRegistry.WORLD_ENTITY_REGISTRY.register("fallingthing", new FallingEntityFactory());
        
        GameRegistry.GENERATOR_REGISTRY.register("STS", new WorldGenerator() {
            
            @Override
            protected void initCaps() {
                this.CAPS.add(GeneratorCapabilitiesBase.LVL_ENTRY);
            }
            
            @Override
            public WorldGenerationBundle generateWorld(long seed) {
                return new WorldGenerationBundle(seed, new WorldMeta(400, 400, true), new TestChunkGenerator(), (g) ->{
                    //g.setLightProvider(AmbientLightProvider.constant(Color.WHITE)); //<- is the default anyways
                    g.addEntity(GameRegistry.BACKGROUND_REGISTRY.get("stars").getEntity());//Hmmmmm... still not optimal i guess?
                });
            }
        });
    }
}
