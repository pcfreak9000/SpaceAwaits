package de.pcfreak9000.spaceawaits.tileworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;

public class WorldScreen extends ScreenAdapter {
    
    private WorldManager worldManager;
    private FPSLogger fps;
    
    public WorldScreen(WorldManager mgr) {
        this.worldManager = mgr;
        this.fps = new FPSLogger();
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //Render Game HUD here?
        this.worldManager.updateAndRender(delta);
        fps.log();
    }
    
    @Override
    public void resize(int width, int height) {
        worldManager.getRenderInfo().resize(width, height);
    }
    
    @Override
    public void hide() {
        this.worldManager.setWorld(null);
    }
}
