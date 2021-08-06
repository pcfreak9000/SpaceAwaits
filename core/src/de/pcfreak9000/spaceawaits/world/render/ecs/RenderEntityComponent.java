package de.pcfreak9000.spaceawaits.world.render.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;

import de.pcfreak9000.spaceawaits.world.render.SpriteRenderPreAction;

public class RenderEntityComponent implements Component {
    
    public Sprite sprite;
    public SpriteRenderPreAction action;
    //Maybe take a texture from somewhere and directly create the sprite and update the texture if neccessary???
}
