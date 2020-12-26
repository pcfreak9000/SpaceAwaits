package de.pcfreak9000.spaceawaits.tileworld;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;

import de.pcfreak9000.spaceawaits.tileworld.ecs.ParallaxComponent;

public class Background {
    
   // private static final TextureConfig BACKGROUND_CONFIG = new TextureConfig().wrappingMode(WrappingMode.Repeat);//TODO move
    
    private final String texture;
    
    private final Entity entity;
    private final Sprite sprite;
    
    public Background(String texture, float aspect, float tilingFactor, float xMov, float yMov) {
        this.texture = texture;
        this.entity = new Entity();
        this.sprite = new Sprite();        
        this.entity.add(new ParallaxComponent(xMov, yMov, aspect));
        this.sprite.setSize(1920 * 2 * aspect, 1920 * 2);
        //this.sprite.getRenderData().setTilingFactor(tilingFactor);
        //this.sprite.setLayer(-2);//TODO layer
    }
    //TODO texture the background
    
    public Entity getEntity() {
        return this.entity;
    }
}
