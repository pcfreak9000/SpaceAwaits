package de.pcfreak9000.spaceawaits.world.render.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.spaceawaits.core.assets.WatchDynamicAsset;

@WatchDynamicAsset
public class RenderRenderableComponent implements Component {
    @WatchDynamicAsset
    public IRenderable renderable;
    
    public boolean dofrustumcheck = true;
    
    public float width, height;
    public Color color;
}
