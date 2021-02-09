package de.pcfreak9000.spaceawaits.world;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;

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
    
    public void setAdditiveBlending() {
        spriteBatch.enableBlending();
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
    }
    
    public void setDefaultBlending() {
        spriteBatch.enableBlending();
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
    
    public void setMultiplicativeBlending() {
        spriteBatch.enableBlending();
        spriteBatch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO);
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
        SpaceAwaits.getSpaceAwaits().getWorldManager().lightCalc.resize(this.viewport.getCamera().viewportWidth, this.viewport.getCamera().viewportHeight);//TODO hacky
    }
}
