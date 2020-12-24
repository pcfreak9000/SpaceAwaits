package de.pcfreak9000.spaceawaits.tileworld.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class RenderComponent implements Component {
    
    public final Sprite sprite;
    
    public RenderComponent(Sprite sprite) {
        this.sprite = sprite;
    }
    
}
