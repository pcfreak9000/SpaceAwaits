package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.gdx.graphics.g2d.Sprite;

import de.pcfreak9000.spaceawaits.core.ITextureProvider;

public class TextureSpriteAction implements SpriteRenderPreAction {
    
    private ITextureProvider texture;
    
    public TextureSpriteAction(ITextureProvider provider) {
        this.texture = provider;
    }
    
    @Override
    public void act(Sprite sprite) {
        sprite.setRegion(texture.getRegion());
    }
}
