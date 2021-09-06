package de.pcfreak9000.spaceawaits.world.render.ecs;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.world.render.strategy.IRenderStrategy;

public class RenderComponent implements Component {
    
    public boolean enabled = true;
    public boolean considerAsGui = false;
    
    public final float layer;
    public final String renderStratId;
    //Cache the render strategy:
    IRenderStrategy renderStrategy;
    
    public RenderComponent(float layer, String rDecId) {
        this.layer = layer;
        this.renderStratId = rDecId;
    }
    
}
