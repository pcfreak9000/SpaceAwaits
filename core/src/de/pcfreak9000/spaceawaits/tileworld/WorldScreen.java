package de.pcfreak9000.spaceawaits.tileworld;

import com.badlogic.gdx.ScreenAdapter;

public class WorldScreen extends ScreenAdapter {
    
    private WorldManager worldManager;
    
    public WorldScreen(WorldManager mgr) {
        this.worldManager = mgr;
    }
    
    @Override
    public void render(float delta) {
        //Render Game HUD here?
        this.worldManager.updateAndRender(delta);
    }
    
    @Override
    public void hide() {
        this.worldManager.setWorld(null);
    }
}
