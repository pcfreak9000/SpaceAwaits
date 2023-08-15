package de.pcfreak9000.spaceawaits.world.render.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

public class RenderRenderableComponent implements Component {
    
    public IRenderable renderable;
    public float width, height;
    public Color color;
}
