package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.core.Player;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.world.ecs.BreakingTileSystem;
import de.pcfreak9000.spaceawaits.world.ecs.BreakingTilesComponent;
import de.pcfreak9000.spaceawaits.world.ecs.CameraSystem;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.PlayerInputSystem;
import de.pcfreak9000.spaceawaits.world.ecs.chunk.TickChunkSystem;
import de.pcfreak9000.spaceawaits.world.ecs.entity.MovingWorldEntitySystem;
import de.pcfreak9000.spaceawaits.world.light.LightCalculator;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsSystemBox2D;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;
import de.pcfreak9000.spaceawaits.world.render.RenderChunkStrategy;
import de.pcfreak9000.spaceawaits.world.render.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.RenderEntityStrategy;
import de.pcfreak9000.spaceawaits.world.render.RenderItemStrategy;
import de.pcfreak9000.spaceawaits.world.render.RenderParallaxStrategy;
import de.pcfreak9000.spaceawaits.world.render.RenderSystem;
import de.pcfreak9000.spaceawaits.world.render.RenderTileBreakingStrategy;

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
        ecs.addSystem(new MovingWorldEntitySystem(this));
        ecs.addSystem(new CameraSystem(this));
        ecs.addSystem(ticketHandler = new TicketedChunkManager(this, (ChunkProvider) chunkProvider));
        RenderSystem rsys = new RenderSystem();
        rsys.registerRenderStrategy("entity", new RenderEntityStrategy());
        rsys.registerRenderStrategy("chunk", new RenderChunkStrategy());
        rsys.registerRenderStrategy("para", new RenderParallaxStrategy(this));
        rsys.registerRenderStrategy("item", new RenderItemStrategy());
        rsys.registerRenderStrategy("break", new RenderTileBreakingStrategy());
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
