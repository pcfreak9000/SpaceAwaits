package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.world.WorldEvents.WorldMetaNBTEvent.Type;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.WorldEntityChunkAdjustSystem;
import de.pcfreak9000.spaceawaits.world.ecs.SystemResolver;
import de.pcfreak9000.spaceawaits.world.ecs.content.ActivatorSystem;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.DynamicAssetUtil;
import de.pcfreak9000.spaceawaits.world.ecs.content.FollowMouseSystem;
import de.pcfreak9000.spaceawaits.world.ecs.content.InventoryOpenerSystem;
import de.pcfreak9000.spaceawaits.world.ecs.content.ParallaxSystem;
import de.pcfreak9000.spaceawaits.world.ecs.content.PlayerInputSystem;
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
    //Server side stuff
    private TicketedChunkManager ticketHandler;
    //Client side stuff
    private GameRenderer gameRenderer;
    
    public WorldCombined(WorldPrimer primer, IWorldSave save, long seed, GameRenderer renderer) {
        super(primer, seed);
        this.gameRenderer = renderer;
        setupECS(primer, ecsEngine);
        ((ChunkLoader) chunkLoader).setSave(save);
        ((UnchunkProvider) unchunkProvider).setSave(save);
        ((UnchunkProvider) unchunkProvider).load();
        this.getWorldBus().post(new WorldEvents.WorldMetaNBTEvent(this.unchunkProvider.worldInfo(), Type.Reading));
        if (worldProperties.autoWorldBorders()) {
            WorldUtil.createWorldBorders(unchunkProvider.get().getEntities(), getBounds().getWidth(),
                    getBounds().getHeight());
        }
        for (Entity e : unchunkProvider.get().getEntities()) {
            DynamicAssetUtil.checkAndCreateAsset(e);//TODO Dyn Meh
            ecsEngine.addEntity(e);
        }
    }
    
    public void saveAll() {
        this.getWorldBus().post(new WorldEvents.WorldMetaNBTEvent(this.unchunkProvider.worldInfo(), Type.Writing));
        ((ChunkProvider) chunkProvider).saveAll();
        ((UnchunkProvider) unchunkProvider).save();
    }
    
    @Override
    protected IChunkLoader createChunkLoader(WorldPrimer primer) {
        return new ChunkLoader(this, primer.getChunkGenerator());
    }
    
    @Override
    protected IChunkProvider createChunkProvider(WorldPrimer primer) {
        return new ChunkProvider(this, chunkLoader);
    }
    
    @Override
    protected IUnchunkProvider createUnchunkProvider(WorldPrimer primer) {
        return new UnchunkProvider(this, primer.getUnchunkGenerator(), primer.getWorldGenerator());
    }
    
    private void setupECS(WorldPrimer primer, Engine engine) {
        SystemResolver ecs = new SystemResolver();
        ecs.addSystem(new InventoryOpenerSystem(gameRenderer, this));
        ecs.addSystem(new TileSystem(this, worldRandom, chunkProvider));
        ecs.addSystem(new PlayerInputSystem(this, this.gameRenderer));
        ecs.addSystem(new ActivatorSystem(gameRenderer, this));
        ecs.addSystem(new FollowMouseSystem(gameRenderer));
        ecs.addSystem(new PhysicsForcesSystem(this));
        PhysicsSystem phsys = new PhysicsSystem(this);
        ecs.addSystem(phsys);
        ecs.addSystem(new WorldEntityChunkAdjustSystem(this));
        ecs.addSystem(new CameraSystem(this));
        ecs.addSystem(ticketHandler = new TicketedChunkManager(this, (ChunkProvider) chunkProvider));
        ecs.addSystem(new ParallaxSystem(this, this.gameRenderer));
        ecs.addSystem(new RenderSystem(this, this.gameRenderer));
        ecs.addSystem(new PhysicsDebugRendererSystem(phsys, this.gameRenderer));
        ecs.addSystem(new TickCounterSystem(this));
        SpaceAwaits.BUS.post(new WorldEvents.SetupEntitySystemsEvent(this, ecs, primer));
        ecs.setupSystems(engine);
    }
    
    @Override
    public void joinWorld(Player player) {
        super.joinWorld(player);
        Vector2 playerpos = Components.TRANSFORM.get(player.getPlayerEntity()).position;
        addTicket(new FollowingTicket(playerpos, 4));
        SpaceAwaits.BUS.post(new WorldEvents.PlayerJoinedEvent(this, player));
    }
    
    public void addTicket(ITicket ticket) {
        this.ticketHandler.addTicket(ticket);
    }
    
    public void removeTicket(ITicket ticket) {
        this.ticketHandler.removeTicket(ticket);
    }
    
}
