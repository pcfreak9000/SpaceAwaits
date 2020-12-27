package de.pcfreak9000.spaceawaits.tileworld;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class WorldRenderInfo {
    
    private OrthographicCamera camera;
    private FitViewport viewport;
    
    private SpriteBatch spriteBatch;
    
    public WorldRenderInfo() {
        this.camera = new OrthographicCamera(1920, 1920);
        this.viewport = new FitViewport(1920, 1080, camera);
        this.spriteBatch = new SpriteBatch(8191);//8191 is the max sadly...
    }
    
    public Camera getCamera() {
        return camera;
    }
    
    public Viewport getViewport() {
        return viewport;
    }
    
    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }
    
    public void applyViewport() {
        viewport.apply();
        this.spriteBatch.setProjectionMatrix(camera.combined);
    }
    
    public void dispose() {
        this.spriteBatch.dispose();
    }
    
    public void resize(int width, int height) {
        this.viewport.update(width, height);
    }
}
