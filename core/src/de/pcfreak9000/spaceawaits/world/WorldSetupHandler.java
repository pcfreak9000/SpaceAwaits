package de.pcfreak9000.spaceawaits.world;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderSystem;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderFogStrategy;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderItemStrategy;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderLiquidTransparentStrategy;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderRenderableStrategy;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderStatsStrategy;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderTileBreakingStrategy;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderTileDefaultStrategy;

public class WorldSetupHandler {

    @EventSubscription
    private void setupRenderStrategeies(RenderSystem.RegisterRenderStrategiesEvent ev) {
        ev.addStrategy(new RenderRenderableStrategy(ev.renderer));
        ev.addStrategy(new RenderTileDefaultStrategy(ev.renderer));
        ev.addStrategy(new RenderItemStrategy(ev.renderer));
        ev.addStrategy(new RenderTileBreakingStrategy(ev.renderer));
        ev.addStrategy(new RenderFogStrategy(ev.renderer));
        ev.addStrategy(new RenderLiquidTransparentStrategy(ev.renderer, ev.world));
        ev.addStrategy(new RenderStatsStrategy(ev.renderer));
        //ev.addStrategy(new RenderFlat(ev.renderer));
    }

}
