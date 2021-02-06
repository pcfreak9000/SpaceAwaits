package de.pcfreak9000.spaceawaits.tileworld.ecs.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;

import de.pcfreak9000.spaceawaits.core.TextureProvider;

public class TextureSpriteAction implements SpriteRenderPreAction {
    
    private TextureProvider texture;
    
    public TextureSpriteAction(TextureProvider provider) {
        this.texture = provider;
    }
    
    @Override
    public void act(Sprite sprite) {
        sprite.setRegion(texture.getRegion());
    }
}
