package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.core.Player;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.TickChunkSystem;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.WorldEntityChunkAdjustSystem;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.PlayerInputSystem;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;
import de.pcfreak9000.spaceawaits.world.light.LightCalculator;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsSystemBox2D;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;
import de.pcfreak9000.spaceawaits.world.render.ecs.CameraSystem;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderSystem;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderChunkStrategy;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderEntityStrategy;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderItemStrategy;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderParallaxStrategy;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderTileBreakingStrategy;
import de.pcfreak9000.spaceawaits.world.tile.ecs.BreakingTileSystem;
import de.pcfreak9000.spaceawaits.world.tile.ecs.BreakingTilesComponent;

public class WorldCombined extends World {
    //Server side stuff
    private TicketedChunkManager ticketHandler;
    //Client side stuff
    private PlayerInputSystem playerInput;
    private GameRenderer gameRenderer;
    
    public WorldCombined(WorldPrimer primer, IWorldSave save, long seed, GameRenderer renderer) {
        super(primer, seed);
        this.gameRenderer = renderer;
        setupECS(primer, ecsEngine);
        ((ChunkProvider) chunkProvider).setSave(save);
        ((UnchunkProvider) unchunkProvider).setSave(save);
        ((UnchunkProvider) unchunkProvider).load();
        if (worldProperties.autoWorldBorders()) {
            WorldUtil.createWorldBorders(unchunkProvider.get().getEntities(), getBounds().getWidth(),
                    getBounds().getHeight());
        }
        for (Entity e : unchunkProvider.get().getEntities()) {
            ecsEngine.addEntity(e);
        }
    }
    
    public void saveAll() {
        ((ChunkProvider) chunkProvider).saveAll();
        ((UnchunkProvider) unchunkProvider).save();
    }
    
    @Override
    protected IChunkProvider createChunkProvider(WorldPrimer primer) {
        return new ChunkProvider(this, primer.getChunkGenerator());
    }
    
    @Override
    protected IUnchunkProvider createUnchunkProvider(WorldPrimer primer) {
        return new UnchunkProvider(this, primer.getUnchunkGenerator());
    }
    
    private void setupECS(WorldPrimer primer, Engine ecs) {
        this.playerInput = new PlayerInputSystem(this, this.gameRenderer);
        ecs.addSystem(playerInput);
        ecs.addSystem(new TickChunkSystem());
        PhysicsSystemBox2D phsys = new PhysicsSystemBox2D(this);
        ecs.addSystem(phsys);
        ecs.addSystem(new WorldEntityChunkAdjustSystem(this));
        ecs.addSystem(new CameraSystem(this));
        ecs.addSystem(ticketHandler = new TicketedChunkManager(this, (ChunkProvider) chunkProvider));
        RenderSystem rsys = new RenderSystem();
        //this sucks
        GameRegistry.RENDER_STRATEGY_REGISTRY.register("entity", new RenderEntityStrategy());
        GameRegistry.RENDER_STRATEGY_REGISTRY.register("chunk", new RenderChunkStrategy());
        GameRegistry.RENDER_STRATEGY_REGISTRY.register("para", new RenderParallaxStrategy(this));
        GameRegistry.RENDER_STRATEGY_REGISTRY.register("item", new RenderItemStrategy());
        GameRegistry.RENDER_STRATEGY_REGISTRY.register("break", new RenderTileBreakingStrategy());
        //******
        ecs.addSystem(rsys);
        LightCalculator lightCalc = new LightCalculator(this);
        ecs.addSystem(lightCalc);
        ecs.addSystem(new BreakingTileSystem());
        //lightCalc.setProcessing(false);
        //ecs.addSystem(new PhysicsDebugRendererSystem(phsys));
        
        ecs.addEntity(createBreakingAnimationsEntity());
    }
    
    @Override
    public void joinWorld(Player player) {
        super.joinWorld(player);
        Vector2 playerpos = CoreRes.TRANSFORM_M.get(player.getPlayerEntity()).position;
        addTicket(new FollowingTicket(playerpos));
        this.playerInput.setPlayer(player);
    }
    
    public Vector2 findSpawnpoint(Player player) {
        Rectangle spawnArea = playerSpawn.getSpawnArea(player);//TODO what about a spawnpoint near a bed? or another disjunct rect?
        RandomXS128 rand = new RandomXS128(this.getSeed());
        ChunkProvider chunkProvider = (ChunkProvider) this.chunkProvider;
        Vector2 playerBounds = player.getPlayerEntity().getComponent(PhysicsComponent.class).factory
                .boundingBoxWidthAndHeight();
        ProbeChunkManager probeChunkMgr = new ProbeChunkManager(chunkProvider, this);
        for (int i = 0; i < 100; i++) {
            float x = spawnArea.x + rand.nextFloat() * spawnArea.width;
            float y = spawnArea.y + rand.nextFloat() * spawnArea.height;
            if (getBounds().inBoundsf(x, y)) {
                int cx = Chunk.toGlobalChunkf(x);
                int cy = Chunk.toGlobalChunkf(y);
                int cw = Chunk.toGlobalChunkf(x + playerBounds.x);
                int ch = Chunk.toGlobalChunkf(y + playerBounds.y);
                for (int j = cx; j <= cw; j++) {
                    for (int k = cy; k <= ch; k++) {
                        probeChunkMgr.loadChunk(j, k, true);
                    }
                }
                if (!checkSolidOccupation(x, y, playerBounds.x, playerBounds.y)) {
                    if (worldProperties.autoLowerSpawnpointToSolidGround()) {
                        while (true) {
                            y--;
                            cy = Chunk.toGlobalChunkf(y);
                            ch = Chunk.toGlobalChunkf(y + playerBounds.y);
                            for (int j = cx; j <= cw; j++) {
                                for (int k = cy; k <= ch; k++) {
                                    probeChunkMgr.loadChunk(j, k, true);
                                }
                            }
                            if (checkSolidOccupation(x, y, playerBounds.x, playerBounds.y)) {// || y < spawnArea.y -> strictly enforcing the spawnArea might lead to fall damage and a death loop 
                                y++;
                                break;
                            }
                        }
                    }
                    //can spawn here, so do that
                    probeChunkMgr.unloadExtraChunks();
                    Logger.getLogger(World.class)
                            .debug("Found a spawning location for the player on the " + (i + 1) + ". try");
                    return new Vector2(x, y);
                }
                if (probeChunkMgr.countExtraChunks() > 13) {
                    probeChunkMgr.unloadExtraChunks();
                }
            }
        }
        probeChunkMgr.unloadExtraChunks();
        Logger.getLogger(World.class).debug("Couldn't find a suitable spawning location.");
        //TODO no spawn was found so just pick a random location and forcefully blow a hole into the ground or something
        return null;
    }
    
    public void addTicket(ITicket ticket) {
        this.ticketHandler.addTicket(ticket);
    }
    
    public void removeTicket(ITicket ticket) {
        this.ticketHandler.removeTicket(ticket);
    }
    
    private Entity createBreakingAnimationsEntity() {
        Entity e = new EntityImproved();
        e.add(new BreakingTilesComponent(this.breakingTiles));
        e.add(new RenderComponent(1, "break"));
        return e;
    }
    
}
