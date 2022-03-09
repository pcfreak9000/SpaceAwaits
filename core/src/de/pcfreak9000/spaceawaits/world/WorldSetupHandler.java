package de.pcfreak9000.spaceawaits.world;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderSystem;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderFogStrategy;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderItemStrategy;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderTextureStrategy;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderTileBreakingStrategy;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderTileDefaultStrategy;

public class WorldSetupHandler {
    
    //    @EventSubscription
    //    public void setup0(WorldEvents.SetupEntitySystemsEvent ev) {
    //        
    //    }
    
    @EventSubscription
    private void setupRenderStrategeies(RenderSystem.RegisterRenderStrategiesEvent ev) {
        ev.renderStrategies.register("entity", new RenderTextureStrategy(ev.renderer));
        ev.renderStrategies.register("tileDefault", new RenderTileDefaultStrategy(ev.renderer));
        ev.renderStrategies.register("item", new RenderItemStrategy(ev.renderer));
        ev.renderStrategies.register("break", new RenderTileBreakingStrategy(ev.renderer));
        ev.renderStrategies.register("fog", new RenderFogStrategy(ev.renderer));
    }
    
}
