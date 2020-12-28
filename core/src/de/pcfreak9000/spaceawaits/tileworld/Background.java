package de.pcfreak9000.spaceawaits.tileworld;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import de.pcfreak9000.spaceawaits.tileworld.ecs.ParallaxComponent;

public class Background {
        
    private final String texture;
    
    private final Entity entity;
    private final Sprite sprite;
    
    public Background(String texture, float width, float height) {
        this.texture = texture;
        this.entity = new Entity();
        this.sprite = new Sprite();        
        this.entity.add(new ParallaxComponent(sprite));
        this.sprite.setSize(width, height);
    }
    
    public Entity getEntity() {
        return this.entity;
    }

    public String getTextureName() {
        return texture;
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        this.sprite.setRegion(textureRegion);
    }
}
