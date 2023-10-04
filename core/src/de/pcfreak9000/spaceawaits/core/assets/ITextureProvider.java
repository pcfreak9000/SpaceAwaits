package de.pcfreak9000.spaceawaits.core.assets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import de.pcfreak9000.spaceawaits.world.render.ecs.IRenderable;

public interface ITextureProvider extends IRenderable {
    
    TextureRegion getRegion();
    
    @Override
    default void render(SpriteBatch batch, float x, float y, float rotoffx, float rotoffy, float width,
            float height, float scaleX, float scaleY, float rotation) {
        batch.draw(getRegion(), x, y, rotoffx, rotoffy, width, height, scaleX, scaleY, rotation);
    }
    
}
