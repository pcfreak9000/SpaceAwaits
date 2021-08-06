package de.pcfreak9000.spaceawaits.world.render.ecs;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.world.render.strategy.IRenderStrategy;

public class RenderComponent implements Component {
    
    public final int layer;
    public final String renderDecoratorId;
    //Cache the render strategy:
    IRenderStrategy renderStrategy;
    
    public RenderComponent(int layer, String rDecId) {
        this.layer = layer;
        this.renderDecoratorId = rDecId;
    }
    
}
