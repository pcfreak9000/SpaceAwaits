package de.pcfreak9000.spaceawaits.tileworld;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.viewport.FillViewport;

public class WorldRenderer {
    private OrthographicCamera camera;
    private FillViewport viewport;
    
    public WorldRenderer() {
        this.camera = new OrthographicCamera(1920, 1920);
        this.viewport = new FillViewport(1920, 1920, camera);
    }
    
    public void add(Sprite sprite) {
        
    }
    
    public void remove(Sprite sprite) {
        
    }

    public Camera getCamera() {
        return camera;
    }
    
    public void render(float deltat) {
        viewport.apply();
    }

    public void dispose() {
        
    }
}
