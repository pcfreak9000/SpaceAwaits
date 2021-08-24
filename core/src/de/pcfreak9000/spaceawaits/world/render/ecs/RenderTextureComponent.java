package de.pcfreak9000.spaceawaits.world.render.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.spaceawaits.core.ITextureProvider;

public class RenderTextureComponent implements Component {
    
    public ITextureProvider texture;
    public float width, height;
    public Color color;
}
