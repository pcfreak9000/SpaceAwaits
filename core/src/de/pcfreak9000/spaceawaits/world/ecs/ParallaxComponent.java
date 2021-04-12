package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;

import de.pcfreak9000.spaceawaits.world.render.SpriteRenderPreAction;

public class ParallaxComponent implements Component {
    
    public Sprite sprite;
    public SpriteRenderPreAction action;
    
}
