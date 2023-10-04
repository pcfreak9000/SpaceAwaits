package de.pcfreak9000.spaceawaits.world.render.ecs;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface IRenderable {
    
    default void render(SpriteBatch batch, float x, float y, float width, float height) {
        render(batch, x, y, 0, 0, width, height, 1, 1, 0);
    }
    
    void render(SpriteBatch batch, float x, float y, float rotoffx, float rotoffy, float width, float height,
            float scaleX, float scaleY, float rotation);
}
