package de.pcfreak9000.spaceawaits.world;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderSystem;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderChunkStrategy;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderEntityStrategy;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderItemStrategy;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderParallaxStrategy;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderTileBreakingStrategy;

public class WorldSetupHandler {
    
    //    @EventSubscription
    //    public void setup0(WorldEvents.SetupEntitySystemsEvent ev) {
    //        
    //    }
    
    @EventSubscription
    private void setupRenderStrategeies(RenderSystem.RegisterRenderStrategiesEvent ev) {
        ev.renderStrategies.register("entity", new RenderEntityStrategy(ev.renderer));
        ev.renderStrategies.register("chunk", new RenderChunkStrategy(ev.renderer));
        ev.renderStrategies.register("para", new RenderParallaxStrategy(ev.world, ev.renderer));
        ev.renderStrategies.register("item", new RenderItemStrategy(ev.renderer));
        ev.renderStrategies.register("break", new RenderTileBreakingStrategy(ev.renderer));
    }
    
}
