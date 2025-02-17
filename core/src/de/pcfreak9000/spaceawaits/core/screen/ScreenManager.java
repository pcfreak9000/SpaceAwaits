package de.pcfreak9000.spaceawaits.core.screen;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.flat.FlatPlayer;
import de.pcfreak9000.spaceawaits.flat.FlatScreen;
import de.pcfreak9000.spaceawaits.flat.FlatWorld;

public class ScreenManager {
    
    private SpaceAwaits space;
    private GuiHelper guiHelper;
    
    public ScreenManager(SpaceAwaits space) {
        this.space = space;
        this.guiHelper = new GuiHelper();
    }
    
    public GuiHelper getGuiHelper() {
        return guiHelper;
    }
    
    public void setMainMenuScreen() {
        this.space.setScreen(new MainMenuScreen(this, guiHelper));
    }
    
    public void setSelectSaveScreen() {
        this.space.setScreen(new SelectSaveScreen(this, guiHelper));
    }
    
    public void setWorldScreen(TileScreen ts) {
        this.space.setScreen(ts);
    }
    
    public void setFlatWorldScreen(FlatWorld world, FlatPlayer fp) {
        this.space.setScreen(new FlatScreen(guiHelper, world, fp));
    }
    
}
