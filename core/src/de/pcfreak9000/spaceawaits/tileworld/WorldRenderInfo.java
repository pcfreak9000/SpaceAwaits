package de.pcfreak9000.spaceawaits.tileworld;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class WorldRenderInfo {
    
    private OrthographicCamera camera;
    private FillViewport viewport;
    
    private SpriteBatch spriteBatch;
    
    public WorldRenderInfo() {
        this.camera = new OrthographicCamera(1920, 1920);
        this.viewport = new FillViewport(1920, 1920, camera);
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
    
    public void prepare() {
        viewport.apply();
        this.spriteBatch.setProjectionMatrix(camera.combined);
    }
    
    public void dispose() {
        this.spriteBatch.dispose();
    }
    
    public void resize(int width, int height) {
        this.viewport.setScreenSize(width, height);
    }
}
