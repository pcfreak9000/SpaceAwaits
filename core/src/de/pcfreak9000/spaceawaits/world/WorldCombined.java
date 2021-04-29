package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.spaceawaits.core.Player;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.world.ecs.CameraSystem;
import de.pcfreak9000.spaceawaits.world.ecs.PlayerInputSystem;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.ecs.chunk.TickChunkSystem;
import de.pcfreak9000.spaceawaits.world.ecs.entity.MovingWorldEntitySystem;
import de.pcfreak9000.spaceawaits.world.light.LightCalculator;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsSystemBox2D;
import de.pcfreak9000.spaceawaits.world.render.RenderChunkStrategy;
import de.pcfreak9000.spaceawaits.world.render.RenderEntityStrategy;
import de.pcfreak9000.spaceawaits.world.render.RenderItemStrategy;
import de.pcfreak9000.spaceawaits.world.render.RenderParallaxStrategy;
import de.pcfreak9000.spaceawaits.world.render.RenderSystem;

public class WorldCombined extends World {
    
    private TicketedChunkManager ticketHandler;
    
    public WorldCombined(WorldPrimer primer, IWorldSave save) {
        super(primer);
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
    
    @Override
    protected void finishSetup(WorldPrimer primer, Engine ecs) {
        ecs.addSystem(new PlayerInputSystem(this));
        ecs.addSystem(new TickChunkSystem());
        PhysicsSystemBox2D phsys = new PhysicsSystemBox2D();
        ecs.addSystem(phsys);
        ecs.addSystem(new MovingWorldEntitySystem(this));
        ecs.addSystem(new CameraSystem(this));
        ecs.addSystem(ticketHandler = new TicketedChunkManager(this, (ChunkProvider) chunkProvider));
        RenderSystem rsys = new RenderSystem();
        rsys.registerRenderDecorator("entity", new RenderEntityStrategy());
        rsys.registerRenderDecorator("chunk", new RenderChunkStrategy());
        rsys.registerRenderDecorator("para", new RenderParallaxStrategy(this));
        rsys.registerRenderDecorator("item", new RenderItemStrategy());
        ecs.addSystem(rsys);
        ecs.addSystem(new LightCalculator(this));
        //lightCalc.setProcessing(false);
        //this.ecsManager.addSystem(new PhysicsDebugRendererSystem(phsys));
        
    }
    
    @Override
    public void joinWorld(Player player) {
        super.joinWorld(player);
        Vector2 playerpos = player.getPlayerEntity().getComponent(TransformComponent.class).position;
        addTicket(new FollowingTicket(playerpos));
    }
    
    public void addTicket(ITicket ticket) {
        this.ticketHandler.addTicket(ticket);
    }
    
    public void removeTicket(ITicket ticket) {
        this.ticketHandler.removeTicket(ticket);
    }
    
}
