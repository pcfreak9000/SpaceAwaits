package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import de.omnikryptec.event.EventBus;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.core.assets.DynamicAssetListener;
import de.pcfreak9000.spaceawaits.core.assets.WatchDynamicAssetAnnotationProcessor;
import de.pcfreak9000.spaceawaits.core.ecs.EngineImproved;
import de.pcfreak9000.spaceawaits.core.ecs.SystemResolver;
import de.pcfreak9000.spaceawaits.core.ecs.content.FollowMouseSystem;
import de.pcfreak9000.spaceawaits.core.ecs.content.GuiOverlaySystem;
import de.pcfreak9000.spaceawaits.core.ecs.content.ParallaxSystem;
import de.pcfreak9000.spaceawaits.core.ecs.content.RandomSystem;
import de.pcfreak9000.spaceawaits.core.ecs.content.TickCounterSystem;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkSystem;
import de.pcfreak9000.spaceawaits.world.ecs.ActivatorSystem;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.ecs.EntityInteractSystem;
import de.pcfreak9000.spaceawaits.world.ecs.InventoryHandlerSystem;
import de.pcfreak9000.spaceawaits.world.ecs.Loadable;
import de.pcfreak9000.spaceawaits.world.ecs.PlayerInputSystem;
import de.pcfreak9000.spaceawaits.world.ecs.RandomTickSystem;
import de.pcfreak9000.spaceawaits.world.ecs.SelectorSystem;
import de.pcfreak9000.spaceawaits.world.ecs.WorldSystem;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsDebugRendererSystem;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsForcesSystem;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsSystem;
import de.pcfreak9000.spaceawaits.world.render.WorldScreen;
import de.pcfreak9000.spaceawaits.world.render.ecs.CameraSystem;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderSystem;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class World {
    
    public static final float STEPLENGTH_SECONDS = 1 / 60f;
    
    protected final EngineImproved ecsEngine;
    
    //Used for random item drops etc, not terrain gen etc
    
    //Server side stuff
    private IGlobalLoader globalLoader;
    private IChunkLoader chunkLoader;
    
    private ITicket currentPlayerTicket;
    //TMP
    private WorldPrimer primer;
    
    public World(WorldPrimer primer, IWorldSave save) {
        //initialize fields
        this.ecsEngine = new EngineImproved(STEPLENGTH_SECONDS);
        SpaceAwaits.BUS.register(this.ecsEngine.getEventBus());//Not too sure about this
        this.primer = primer;
        this.globalLoader = save.createGlobalLoader();
        this.chunkLoader = save.createChunkLoader();
    }
    
    public void update(float dt) {
        this.ecsEngine.update(dt);
    }
    
    public void setPlayer(Player player) {
        ecsEngine.addEntity(player.getPlayerEntity());
        Vector2 playerpos = Components.TRANSFORM.get(player.getPlayerEntity()).position;
        addTicket(currentPlayerTicket = new FollowingTicket(playerpos, 2));
        ecsEngine.getEventBus().post(new WorldEvents.PlayerJoinedEvent(player));
    }
    
    //Could keep the Player instance here and just removePlayer()...
    public void removePlayer(Player player) {
        ecsEngine.removeEntity(player.getPlayerEntity());
        removeTicket(currentPlayerTicket);
        currentPlayerTicket = null;
        ecsEngine.getEventBus().post(new WorldEvents.PlayerLeftEvent(player));
    }
    
    public <T extends EntitySystem> T getSystem(Class<T> clazz) {
        return ecsEngine.getSystem(clazz);
    }
    
    public EventBus getWorldBus() {
        return this.ecsEngine.getEventBus();
    }
    
    public void initRenderableWorld(WorldScreen screen) {
        setupECS(ecsEngine, screen, primer);
        
        EntitySystem[] syss = ecsEngine.getSystems().toArray(EntitySystem.class);
        for (EntitySystem es : syss) {
            if (es instanceof Loadable) {
                Loadable u = (Loadable) es;
                u.load();
            }
        }
    }
    
    public void saveWorld() {
        chunkLoader.saveAllChunks();
        globalLoader.save();
    }
    
    public void unloadWorld() {
        //can't use ecsEngine.removeAllSystems(); because systems need to be unregistered
        EntitySystem[] syss = ecsEngine.getSystems().toArray(EntitySystem.class);
        for (EntitySystem es : syss) {
            if (es instanceof Loadable) {
                Loadable u = (Loadable) es;
                u.unload();
            }
        }
        ecsEngine.removeAllEntities();
        
        //first decouple...
        for (EntitySystem es : syss) {
            ecsEngine.removeSystem(es);
            SpaceAwaits.BUS.unregister(es);//Forcefully unregister systems which would otherwise be dangling. Systems shouldn't register to this BUS anyways, at least usually. 
        }
        //...then dispose
        for (EntitySystem es : syss) {
            if (es instanceof Disposable) {
                Disposable d = (Disposable) es;
                d.dispose();
            }
        }
        SpaceAwaits.BUS.unregister(ecsEngine.getEventBus());
        for (DynamicAssetListener<Component> dal : WatchDynamicAssetAnnotationProcessor.get()) {
            ecsEngine.removeEntityListener(dal);
        }
        this.chunkLoader.finish();
        this.globalLoader.finish();
    }
    
    private void setupECS(Engine engine, WorldScreen gameScreen, WorldPrimer primer) {
        for (DynamicAssetListener<Component> dal : WatchDynamicAssetAnnotationProcessor.get()) {
            engine.addEntityListener(dal.getFamily(), dal);
        }
        SystemResolver ecs = new SystemResolver();
        ecs.addSystem(new WorldSystem(globalLoader, primer.getWorldGenerator(), primer.getWorldBounds(),
                primer.getWorldProperties(), primer.getLightProvider()));
        ecs.addSystem(new InventoryHandlerSystem());
        ecs.addSystem(new TileSystem());
        ecs.addSystem(new PlayerInputSystem());
        ecs.addSystem(new SelectorSystem());
        ecs.addSystem(new ActivatorSystem());
        ecs.addSystem(new FollowMouseSystem());
        ecs.addSystem(new EntityInteractSystem());
        ecs.addSystem(new PhysicsForcesSystem());
        ecs.addSystem(new PhysicsSystem());
        ecs.addSystem(new ChunkSystem(chunkLoader, primer.getChunkGenerator(), primer.getWorldBounds(),
                primer.getWorldProperties()));
        ecs.addSystem(new CameraSystem(primer.getWorldBounds(), gameScreen.getRenderHelper()));
        ecs.addSystem(new ParallaxSystem(CameraSystem.VISIBLE_TILES_MIN));
        ecs.addSystem(new RenderSystem(ecsEngine, gameScreen));
        ecs.addSystem(new PhysicsDebugRendererSystem());
        ecs.addSystem(new TickCounterSystem());
        ecs.addSystem(new RandomTickSystem());
        ecs.addSystem(new GuiOverlaySystem(gameScreen));
        ecs.addSystem(new RandomSystem(new RandomXS128()));
        //this one needs some stuff with topological sort anyways to resolve dependencies etc
        //SpaceAwaits.BUS.post(new WorldEvents.SetupEntitySystemsEvent(this, ecs, primer));
        ecs.setupSystems(engine);
    }
    
    public void addTicket(ITicket ticket) {
        ecsEngine.getSystem(ChunkSystem.class).addTicket(ticket);
    }
    
    public void removeTicket(ITicket ticket) {
        ecsEngine.getSystem(ChunkSystem.class).removeTicket(ticket);
    }
}
