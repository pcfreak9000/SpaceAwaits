package de.pcfreak9000.spaceawaits.world2;

import com.badlogic.ashley.core.Engine;
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
    
    private IWorldSave worldSave;
    private TicketedChunkManager ticketHandler;
    
    public WorldCombined(WorldPrimer primer, IWorldSave save) {
        super(primer);
        this.worldSave = save;
    }
    
    @Override
    protected IChunkProvider createChunkProvider(WorldPrimer primer) {
        return new ChunkProvider(this, primer.getChunkGenerator(), worldSave);
    }
    
    @Override
    protected void setupECS(WorldPrimer primer, Engine ecs) {
        ecs.addSystem(new PlayerInputSystem(this));
        ecs.addSystem(new TickChunkSystem());
        PhysicsSystemBox2D phsys = new PhysicsSystemBox2D();
        ecs.addSystem(phsys);
        ecs.addSystem(new MovingWorldEntitySystem());
        ecs.addSystem(new CameraSystem());
        ecs.addSystem(ticketHandler = new TicketedChunkManager(this, (ChunkProvider) chunkProvider));
        RenderSystem rsys = new RenderSystem();
        rsys.registerRenderDecorator("entity", new RenderEntityStrategy());
        rsys.registerRenderDecorator("chunk", new RenderChunkStrategy());
        rsys.registerRenderDecorator("para", new RenderParallaxStrategy());
        rsys.registerRenderDecorator("item", new RenderItemStrategy());
        ecs.addSystem(rsys);
        ecs.addSystem(new LightCalculator());
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
