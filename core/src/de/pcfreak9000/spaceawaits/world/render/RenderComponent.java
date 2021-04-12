package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.ashley.core.Component;

public class RenderComponent implements Component {
    
    public final int layer;
    public final String renderDecoratorId;
    //Cache the render decorator:
    IRenderDecorator renderDecorator;
    
    public RenderComponent(int layer, String rDecId) {
        this.layer = layer;
        this.renderDecoratorId = rDecId;
    }
    
}
