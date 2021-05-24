package de.pcfreak9000.spaceawaits.menu;

import com.badlogic.gdx.utils.Disposable;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;

public class ScreenManager implements Disposable {
    
    private SpaceAwaits space;
    private GuiHelper guiHelper;
    
    private MainMenuScreen mainMenu;
    private SelectSaveScreen selectSaveScreen;
    private GameRenderer worldScreen;
    
    public ScreenManager(SpaceAwaits space) {
        this.space = space;
        this.guiHelper = new GuiHelper();
        
        worldScreen = new GameRenderer(this, guiHelper);
        mainMenu = new MainMenuScreen(this, guiHelper);
        selectSaveScreen = new SelectSaveScreen(this, guiHelper);
    }
    
    public void setMainMenuScreen() {
        this.space.setScreen(this.mainMenu);
    }
    
    public void setSelectSaveScreen() {
        this.space.setScreen(this.selectSaveScreen);
    }
    
    public void setGameScreen() {
        this.space.setScreen(this.worldScreen);
    }
    
    public GameRenderer getGameRenderer() {
        return this.worldScreen;
    }
    
    public MainMenuScreen getMainMenu() {
        return this.mainMenu;
    }
    
    public SelectSaveScreen getSelectSave() {
        return this.selectSaveScreen;
    }
    
    @Override
    public void dispose() {
        this.selectSaveScreen.dispose();
        this.mainMenu.dispose();
    }
}
