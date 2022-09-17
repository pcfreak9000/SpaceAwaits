package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.WorldEntityChunkAdjustSystem;
import de.pcfreak9000.spaceawaits.world.ecs.SystemResolver;
import de.pcfreak9000.spaceawaits.world.ecs.content.ActivatorSystem;
import de.pcfreak9000.spaceawaits.world.ecs.content.BreakingSystem;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.EntityInteractSystem;
import de.pcfreak9000.spaceawaits.world.ecs.content.FollowMouseSystem;
import de.pcfreak9000.spaceawaits.world.ecs.content.InventoryOpenerSystem;
import de.pcfreak9000.spaceawaits.world.ecs.content.ParallaxSystem;
import de.pcfreak9000.spaceawaits.world.ecs.content.PlayerInputSystem;
import de.pcfreak9000.spaceawaits.world.ecs.content.RandomTickSystem;
import de.pcfreak9000.spaceawaits.world.ecs.content.TickCounterSystem;
import de.pcfreak9000.spaceawaits.world.ecs.content.TicketedChunkManager;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsDebugRendererSystem;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsForcesSystem;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsSystem;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;
import de.pcfreak9000.spaceawaits.world.render.ecs.CameraSystem;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderSystem;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class WorldCombined extends World {
    
    private final ChunkProvider chunkProvider;
    private final ChunkLoader chunkLoader;
    private final UnchunkProvider unchunkProvider;
    
    //Server side stuff
    private TicketedChunkManager ticketHandler;
    //Client side stuff
    private GameRenderer gameRenderer;
    
    public WorldCombined(WorldPrimer primer, IWorldSave save, long seed, GameRenderer renderer) {
        super(primer, seed);
        this.gameRenderer = renderer;
        this.chunkLoader = new ChunkLoader(save, this);
        this.unchunkProvider = new UnchunkProvider(save, this, primer.getWorldGenerator());
        this.chunkProvider = new ChunkProvider(this, chunkLoader, primer.getChunkGenerator());
        setupECS(primer, ecsEngine);
        unchunkProvider.load();//TODO Move this?
        if (worldProperties.autoWorldBorders()) {
            WorldUtil.createWorldBorders(this, getBounds().getWidth(), getBounds().getHeight());
        }
    }
    
    public void saveWorld() {
        chunkProvider.saveAll();
        unchunkProvider.save();
    }
    
    @Override
    public void unloadWorld() {
        chunkProvider.unloadAll();
        unchunkProvider.unload();
        ecsEngine.removeAllEntities();
        EntitySystem[] syss = ecsEngine.getSystems().toArray(EntitySystem.class);
        for (EntitySystem es : syss) {
            ecsEngine.removeSystem(es);
            SpaceAwaits.BUS.unregister(es);//Forcefully unregister systems which would otherwise be dangling 
            if (es instanceof Disposable) {
                Disposable d = (Disposable) es;
                d.dispose();
            }
        }
        SpaceAwaits.BUS.unregister(eventBus);
    }
    
    private void setupECS(WorldPrimer primer, Engine engine) {
        SystemResolver ecs = new SystemResolver();
        ecs.addSystem(new InventoryOpenerSystem(gameRenderer, this));
        ecs.addSystem(new EntityInteractSystem(this, chunkProvider, unchunkProvider));
        ecs.addSystem(new TileSystem(this, worldRandom, chunkProvider));
        ecs.addSystem(new PlayerInputSystem(this, this.gameRenderer));
        ecs.addSystem(new ActivatorSystem(gameRenderer, this));
        ecs.addSystem(new FollowMouseSystem(gameRenderer));
        ecs.addSystem(new BreakingSystem());
        ecs.addSystem(new PhysicsForcesSystem(this));
        PhysicsSystem phsys = new PhysicsSystem(this, chunkProvider);
        ecs.addSystem(phsys);
        ecs.addSystem(new WorldEntityChunkAdjustSystem(chunkProvider));
        ecs.addSystem(new CameraSystem(this));
        ecs.addSystem(ticketHandler = new TicketedChunkManager(this, chunkProvider));
        ecs.addSystem(new ParallaxSystem(this, this.gameRenderer));
        ecs.addSystem(new RenderSystem(this, this.gameRenderer));
        ecs.addSystem(new PhysicsDebugRendererSystem(phsys, this.gameRenderer));
        ecs.addSystem(new TickCounterSystem(this));
        ecs.addSystem(new RandomTickSystem(worldRandom, this));
        SpaceAwaits.BUS.post(new WorldEvents.SetupEntitySystemsEvent(this, ecs, primer));
        ecs.setupSystems(engine);
        new DynamicAssetListener().register(engine);
    }
    
    @Override
    public void joinWorld(Player player) {
        super.joinWorld(player);
        Vector2 playerpos = Components.TRANSFORM.get(player.getPlayerEntity()).position;
        addTicket(new FollowingTicket(playerpos, 4));
        SpaceAwaits.BUS.post(new WorldEvents.PlayerJoinedEvent(this, player));
    }
    
    public int getLoadedChunksCount() {
        return chunkProvider.getLoadedChunkCount();
    }
    
    public void addTicket(ITicket ticket) {
        this.ticketHandler.addTicket(ticket);
    }
    
    public void removeTicket(ITicket ticket) {
        this.ticketHandler.removeTicket(ticket);
    }
    
}
