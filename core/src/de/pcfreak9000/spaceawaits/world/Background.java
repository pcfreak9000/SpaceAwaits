package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;

import de.pcfreak9000.spaceawaits.core.TextureProvider;
import de.pcfreak9000.spaceawaits.world.ecs.ParallaxComponent;
import de.pcfreak9000.spaceawaits.world.ecs.entity.TextureSpriteAction;

public class Background implements WorldEntityFactory {
    
    private final TextureProvider texture;
    
    private final float width;
    private final float height;
    
    public Background(TextureProvider texture, float width, float height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public Entity createEntity() {
        Entity e = new Entity();
        ParallaxComponent pc = new ParallaxComponent();
        pc.sprite = new Sprite();
        pc.sprite.setSize(width, height);
        pc.action = new TextureSpriteAction(texture);
        e.add(pc);
        return e;
    }
}
