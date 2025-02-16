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
import de.pcfreak9000.spaceawaits.core.ecs.content.FollowMouseSystem;
import de.pcfreak9000.spaceawaits.core.ecs.content.GuiOverlaySystem;
import de.pcfreak9000.spaceawaits.core.ecs.content.ParallaxSystem;
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

public class WorldCombined extends World {
    
    //Server side stuff
    private IGlobalLoader globalLoader;
    private IChunkLoader chunkLoader;
    
    private ITicket currentPlayerTicket;
    //TMP
    private WorldPrimer primer;
    
    public WorldCombined(WorldPrimer primer, IWorldSave save) {
        super(primer);
        this.primer = primer;
        this.globalLoader = save.createGlobalLoader();
        this.chunkLoader = save.createChunkLoader();
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
        if (worldProperties.autoWorldBorders()) {
            WorldUtil.createWorldBorders(ecsEngine, getBounds().getWidth(), getBounds().getHeight());
        }
    }
    
    public void saveWorld() {
        chunkLoader.saveAllChunks();
        globalLoader.save();
    }
    
    @Override
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
        SpaceAwaits.BUS.unregister(getWorldBus());
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
        ecs.addSystem(new WorldSystem(globalLoader, primer.getWorldGenerator()));
        ecs.addSystem(new InventoryHandlerSystem());
        ecs.addSystem(new TileSystem(this));
        ecs.addSystem(new PlayerInputSystem(this));
        ecs.addSystem(new SelectorSystem());
        ecs.addSystem(new ActivatorSystem());
        ecs.addSystem(new FollowMouseSystem());
        ecs.addSystem(new EntityInteractSystem(this, globalLoader));
        ecs.addSystem(new PhysicsForcesSystem());
        ecs.addSystem(new PhysicsSystem());
        ecs.addSystem(new ChunkSystem(this, this.getBounds(), chunkLoader, primer.getChunkGenerator()));
        ecs.addSystem(new CameraSystem(this.getBounds(), gameScreen.getRenderHelper()));
        ecs.addSystem(new ParallaxSystem(CameraSystem.VISIBLE_TILES_MIN));
        ecs.addSystem(new RenderSystem(this, gameScreen));
        ecs.addSystem(new PhysicsDebugRendererSystem());
        ecs.addSystem(new TickCounterSystem());
        ecs.addSystem(new RandomTickSystem(getWorldRandom(), this));
        ecs.addSystem(new GuiOverlaySystem(gameScreen));
        //this one needs some stuff with topological sort anyways to resolve dependencies etc
        //SpaceAwaits.BUS.post(new WorldEvents.SetupEntitySystemsEvent(this, ecs, primer));
        ecs.setupSystems(engine);
    }
    
    @Override
    public void setPlayer(Player player) {
        super.setPlayer(player);
        this.getSystem(GuiOverlaySystem.class).setPlayer(player);
        Vector2 playerpos = Components.TRANSFORM.get(player.getPlayerEntity()).position;
        addTicket(currentPlayerTicket = new FollowingTicket(playerpos, 2));
        getWorldBus().post(new WorldEvents.PlayerJoinedEvent(this, player));
    }
    
    @Override
    public void removePlayer(Player player) {
        super.removePlayer(player);
        removeTicket(currentPlayerTicket);
        currentPlayerTicket = null;
        getWorldBus().post(new WorldEvents.PlayerLeftEvent(this, player));
    }
    
    public void addTicket(ITicket ticket) {
        ecsEngine.getSystem(ChunkSystem.class).addTicket(ticket);
    }
    
    public void removeTicket(ITicket ticket) {
        ecsEngine.getSystem(ChunkSystem.class).removeTicket(ticket);
    }
    
}
