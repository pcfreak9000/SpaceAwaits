package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.core.assets.DynamicAssetListener;
import de.pcfreak9000.spaceawaits.core.assets.WatchDynamicAssetAnnotationProcessor;
import de.pcfreak9000.spaceawaits.core.ecs.SystemResolver;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.WorldEntityChunkAdjustSystem;
import de.pcfreak9000.spaceawaits.world.ecs.ActivatorSystem;
import de.pcfreak9000.spaceawaits.world.ecs.BreakingSystem;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.ecs.EntityInteractSystem;
import de.pcfreak9000.spaceawaits.world.ecs.FollowMouseSystem;
import de.pcfreak9000.spaceawaits.world.ecs.InventoryOpenerSystem;
import de.pcfreak9000.spaceawaits.world.ecs.ParallaxSystem;
import de.pcfreak9000.spaceawaits.world.ecs.PlayerInputSystem;
import de.pcfreak9000.spaceawaits.world.ecs.RandomTickSystem;
import de.pcfreak9000.spaceawaits.world.ecs.TickCounterSystem;
import de.pcfreak9000.spaceawaits.world.ecs.TicketedChunkManager;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsDebugRendererSystem;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsForcesSystem;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsSystem;
import de.pcfreak9000.spaceawaits.world.render.WorldScreen;
import de.pcfreak9000.spaceawaits.world.render.ecs.CameraSystem;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderSystem;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class WorldCombined extends World {
    
    private final IWorldChunkProvider chunkProvider;
    private final ChunkLoader chunkLoader;
    private final UnchunkProvider unchunkProvider;
    
    //Server side stuff
    private TicketedChunkManager ticketHandler;
    
    private ITicket currentPlayerTicket;
    
    public WorldCombined(WorldPrimer primer, IWorldSave save) {
        super(primer);
        this.chunkLoader = new ChunkLoader(save, this);
        this.unchunkProvider = new UnchunkProvider(save, this, primer.getWorldGenerator());
        this.chunkProvider = new TestChunkProvider(this, chunkLoader, primer.getChunkGenerator());
    }
    
    public void initRenderableWorld(WorldScreen screen) {
        setupECS(ecsEngine, screen);
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
        //can't use ecsEngine.removeAllSystems(); because systems need to be unregistered
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
        for (DynamicAssetListener<Component> dal : WatchDynamicAssetAnnotationProcessor.get()) {
            ecsEngine.removeEntityListener(dal);
        }
        this.chunkLoader.finish();
    }
    
    private void setupECS(Engine engine, WorldScreen gameScreen) {
        for (DynamicAssetListener<Component> dal : WatchDynamicAssetAnnotationProcessor.get()) {
            engine.addEntityListener(dal.getFamily(), dal);
        }
        SystemResolver ecs = new SystemResolver();
        ecs.addSystem(new InventoryOpenerSystem(gameScreen, this));
        ecs.addSystem(new EntityInteractSystem(this, chunkProvider, unchunkProvider));
        ecs.addSystem(new TileSystem(this, chunkProvider));
        ecs.addSystem(new PlayerInputSystem(this, gameScreen));
        ecs.addSystem(new ActivatorSystem(gameScreen, this));
        ecs.addSystem(new FollowMouseSystem(gameScreen));
        //ecs.addSystem(new MoveTestSystem());
        ecs.addSystem(new BreakingSystem());
        ecs.addSystem(new PhysicsForcesSystem(this));
        PhysicsSystem phsys = new PhysicsSystem(this, chunkProvider);
        ecs.addSystem(phsys);
        ecs.addSystem(new WorldEntityChunkAdjustSystem(chunkProvider));
        ecs.addSystem(new CameraSystem(this, gameScreen));
        ecs.addSystem(ticketHandler = new TicketedChunkManager(this, chunkProvider));
        ecs.addSystem(new ParallaxSystem(gameScreen));
        ecs.addSystem(new RenderSystem(this, gameScreen));
        ecs.addSystem(new PhysicsDebugRendererSystem(phsys, gameScreen));
        ecs.addSystem(new TickCounterSystem(this));
        ecs.addSystem(new RandomTickSystem(getWorldRandom(), this));
        //this one needs some stuff with topological sort anyways to resolve dependencies etc
        //SpaceAwaits.BUS.post(new WorldEvents.SetupEntitySystemsEvent(this, ecs, primer));
        ecs.setupSystems(engine);
    }
    
    @Override
    public void setPlayer(Player player) {
        super.setPlayer(player);
        Vector2 playerpos = Components.TRANSFORM.get(player.getPlayerEntity()).position;
        addTicket(currentPlayerTicket = new FollowingTicket(playerpos, 2));
        SpaceAwaits.BUS.post(new WorldEvents.PlayerJoinedEvent(this, player));
    }
    
    @Override
    public void removePlayer(Player player) {
        super.removePlayer(player);
        removeTicket(currentPlayerTicket);
        currentPlayerTicket = null;
        SpaceAwaits.BUS.post(new WorldEvents.PlayerLeftEvent(this, player));
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
