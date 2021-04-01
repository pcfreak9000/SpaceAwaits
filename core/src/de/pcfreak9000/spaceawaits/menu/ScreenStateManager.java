package de.pcfreak9000.spaceawaits.menu;

import com.badlogic.gdx.utils.Disposable;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.WorldRenderer;

public class ScreenStateManager implements Disposable {
    
    private WorldRenderer worldScreen;
    
    private GuiScreenManager guiScreenManager;
    
    private final SpaceAwaits space;
    
    public ScreenStateManager(SpaceAwaits space) {
        this.space = space;
        this.guiScreenManager = new GuiScreenManager(this);
        this.worldScreen = new WorldRenderer(this);
    }
    
    public WorldRenderer getWorldRenderer() {
        return this.worldScreen;
    }
    
    public void setMainMenuScreen() {
        this.space.setScreen(this.guiScreenManager.getMainMenu());
    }
    
    public void setSelectSaveScreen() {
        this.space.setScreen(this.guiScreenManager.getSelectSave());
    }
    
    public void setWorldScreen() {
        this.space.setScreen(worldScreen);
    }
    
    @Override
    public void dispose() {
        this.worldScreen.dispose();
        this.guiScreenManager.dispose();
    }
}
