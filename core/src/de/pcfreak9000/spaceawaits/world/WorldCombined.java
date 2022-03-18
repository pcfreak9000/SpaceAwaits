package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.core.Player;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.TickChunkSystem;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.WorldEntityChunkAdjustSystem;
import de.pcfreak9000.spaceawaits.world.ecs.DynamicAssetUtil;
import de.pcfreak9000.spaceawaits.world.ecs.FollowMouseSystem;
import de.pcfreak9000.spaceawaits.world.ecs.ParallaxSystem;
import de.pcfreak9000.spaceawaits.world.ecs.PlayerInputSystem;
import de.pcfreak9000.spaceawaits.world.ecs.SystemResolver;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
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
        if (worldProperties.autoWorldBorders()) {
            WorldUtil.createWorldBorders(unchunkProvider.get().getEntities(), getBounds().getWidth(),
                    getBounds().getHeight());
        }
        for (Entity e : unchunkProvider.get().getEntities()) {
            DynamicAssetUtil.checkAndCreateAsset(e);//TODO Dyn Meh
            ecsEngine.addEntity(e);
        }
        init();
    }
    
    public void saveAll() {
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
        return new UnchunkProvider(this, primer.getUnchunkGenerator());
    }
    
    private void setupECS(WorldPrimer primer, Engine engine) {
        SystemResolver ecs = new SystemResolver();
        ecs.addSystem(new TileSystem(this, worldRandom, chunkProvider));
        ecs.addSystem(new PlayerInputSystem(this, this.gameRenderer));
        ecs.addSystem(new FollowMouseSystem(gameRenderer));
        ecs.addSystem(new TickChunkSystem());
        ecs.addSystem(new PhysicsForcesSystem(this));
        PhysicsSystem phsys = new PhysicsSystem(this);
        ecs.addSystem(phsys);
        ecs.addSystem(new WorldEntityChunkAdjustSystem(this));
        ecs.addSystem(new CameraSystem(this));
        ecs.addSystem(ticketHandler = new TicketedChunkManager(this, (ChunkProvider) chunkProvider));
        ecs.addSystem(new ParallaxSystem(this, this.gameRenderer));
        ecs.addSystem(new RenderSystem(this, this.gameRenderer));
        //LightCalculator lightCalc = new LightCalculator(this);
        //ecs.addSystem(lightCalc);
        //lightCalc.setProcessing(false);
        ecs.addSystem(new PhysicsDebugRendererSystem(phsys, this.gameRenderer));
        SpaceAwaits.BUS.post(new WorldEvents.SetupEntitySystemsEvent(this, ecs, primer));
        ecs.setupSystems(engine);
    }
    
    @Override
    public void joinWorld(Player player) {
        super.joinWorld(player);
        Vector2 playerpos = CoreRes.TRANSFORM_M.get(player.getPlayerEntity()).position;
        addTicket(new FollowingTicket(playerpos, 4));
        SpaceAwaits.BUS.post(new WorldEvents.PlayerJoinedEvent(this, player));
    }
    
    public Vector2 findSpawnpoint(Player player) {
        Rectangle spawnArea = playerSpawn.getSpawnArea(player);//TODO what about a spawnpoint near a bed? or another disjunct rect?
        RandomXS128 rand = new RandomXS128(this.getSeed());
        ChunkProvider chunkProvider = (ChunkProvider) this.chunkProvider;
        Vector2 playerBounds = player.getPlayerEntity().getComponent(PhysicsComponent.class).factory
                .boundingBoxWidthAndHeight();
        TileSystem ts = ecsEngine.getSystem(TileSystem.class);
        Object lock = new Object();
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
                        chunkProvider.requireChunk(j, k, true, lock);
                    }
                }
                if (!ts.checkSolidOccupation(x, y, playerBounds.x, playerBounds.y)) {
                    if (worldProperties.autoLowerSpawnpointToSolidGround()) {
                        while (true) {
                            y--;
                            cy = Chunk.toGlobalChunkf(y);
                            ch = Chunk.toGlobalChunkf(y + playerBounds.y);
                            for (int j = cx; j <= cw; j++) {
                                for (int k = cy; k <= ch; k++) {
                                    chunkProvider.requireChunk(j, k, true, lock);
                                }
                            }
                            if (ts.checkSolidOccupation(x, y, playerBounds.x, playerBounds.y)) {// || y < spawnArea.y -> strictly enforcing the spawnArea might lead to fall damage and a death loop 
                                y++;
                                break;
                            }
                        }
                    }
                    //can spawn here, so do that
                    chunkProvider.releaseLock(lock);
                    Logger.getLogger(World.class)
                            .debug("Found a spawning location for the player on the " + (i + 1) + ". try");
                    return new Vector2(x, y);
                }
                if (chunkProvider.getLoadedChunkCountLock(lock) > 13) {
                    chunkProvider.releaseLock(lock);
                }
            }
        }
        chunkProvider.releaseLock(lock);
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
    
}
