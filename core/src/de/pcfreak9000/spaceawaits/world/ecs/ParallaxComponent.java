package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class ParallaxComponent implements Component {
    
    public Sprite sprite;
    
    public ParallaxComponent(Sprite sprite) {
        this.sprite = sprite;
    }
    
}
