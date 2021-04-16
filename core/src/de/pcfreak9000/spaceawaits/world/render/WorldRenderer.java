package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.menu.ScreenManager;
import de.pcfreak9000.spaceawaits.world.WorldManager;

public class WorldRenderer extends ScreenAdapter {
    
    private ScreenManager gsm;
    
    private WorldManager worldManager;
    private FPSLogger fps;
    
    private OrthographicCamera camera;
    private FitViewport viewport;
    
    private SpriteBatch spriteBatch;
    
    public WorldRenderer(ScreenManager gsm) {
        this.gsm = gsm;
        this.fps = new FPSLogger();
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(1920 / 24, 1080 / 24, camera);
        this.spriteBatch = new SpriteBatch(8191);//8191 is the max sadly...
    }
    
    public void setWorldManager(WorldManager mgr) {
        this.worldManager = mgr;
    }
    
    public OrthographicCamera getCamera() {
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
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        applyViewport();
        SpaceAwaits.BUS.post(new RendererEvents.UpdateAnimationEvent(delta));
        this.worldManager.updateAndRender(delta);
        this.gsm.getHud().actAndDraw(delta);
        fps.log();
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            SpaceAwaits.getSpaceAwaits().getGameManager().unloadGame();//oof still...
            gsm.setMainMenuScreen();
        }
    }
    
    @Override
    public void resize(int width, int height) {
        this.viewport.update(width, height);
        this.gsm.resize(width, height);
        SpaceAwaits.BUS.post(new RendererEvents.ResizeWorldRendererEvent(this, width, height));
    }
    
    @Override
    public void dispose() {
        super.dispose();
        this.spriteBatch.dispose();
    }
}
