package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.Vector2;

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
        this.getWorldBus().post(new WorldEvents.WMNBTReadingEvent(this.unchunkProvider.worldInfo()));
        if (worldProperties.autoWorldBorders()) {
            WorldUtil.createWorldBorders(this, getBounds().getWidth(), getBounds().getHeight());
        }
    }
    
    public void saveAll() {
        this.getWorldBus().post(new WorldEvents.WMNBTWritingEvent(this.unchunkProvider.worldInfo()));
        ((ChunkProvider) chunkProvider).saveAll();
        ((UnchunkProvider) unchunkProvider).save();
    }
    
    @Override
    protected IChunkLoader createChunkLoader(WorldPrimer primer) {
        return new ChunkLoader(this);
    }
    
    @Override
    protected IChunkProvider createChunkProvider(WorldPrimer primer) {
        return new ChunkProvider(this, chunkLoader, primer.getChunkGenerator());
    }
    
    @Override
    protected IUnchunkProvider createUnchunkProvider(WorldPrimer primer) {
        return new UnchunkProvider(this, primer.getWorldGenerator());
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
        ecs.addSystem(ticketHandler = new TicketedChunkManager(this, (ChunkProvider) chunkProvider));
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
    
    public void addTicket(ITicket ticket) {
        this.ticketHandler.addTicket(ticket);
    }
    
    public void removeTicket(ITicket ticket) {
        this.ticketHandler.removeTicket(ticket);
    }
    
}
