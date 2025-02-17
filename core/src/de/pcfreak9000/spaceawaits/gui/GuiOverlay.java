package de.pcfreak9000.spaceawaits.gui;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.ecs.content.GuiOverlaySystem;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.player.Player;

public class GuiOverlay implements Disposable {
    //Table? Actor? Stage!
    
    protected GameScreen gameScreen;
    protected Player player;
    protected Stage stage;
    
    protected boolean reactsToToggleInventory = true;
    
    //Crappy workaround
    private boolean justOpened = true;
    
    public final void createAndOpen(Player player) {
        Screen currentScreen = SpaceAwaits.getSpaceAwaits().getScreen();
        if (!(currentScreen instanceof GameScreen)) {
            throw new IllegalStateException("Current screen is not a GameScreen");
        }
        this.gameScreen = (GameScreen) currentScreen;
        this.player = player;
        this.stage = this.gameScreen.getGuiHelper().createStage();
        create();
        this.gameScreen.getSystem(GuiOverlaySystem.class).setGuiCurrent(this);
    }
    
    protected void create() {
        
    }
    
    protected void closeContainer() {
        this.gameScreen.getSystem(GuiOverlaySystem.class).setGuiCurrent(null);
    }
    
    public void onOpened() {
        
    }
    
    public void onClosed() {
        
    }
    
    public void actAndDraw(float dt) {
        this.gameScreen.getGuiHelper().drawDarken(0.7f);
        this.gameScreen.getGuiHelper().actAndDraw(stage, dt);
        if (!justOpened && (InptMgr.isJustPressed(EnumInputIds.Esc)
                || (InptMgr.isJustPressed(EnumInputIds.ToggleInventory) && reactsToToggleInventory))) {
            closeContainer();
        }
        if (justOpened) {
            justOpened = false;
        }
    }
    
    protected boolean justOpened() {
        return justOpened;
    }
    
    public Stage getStage() {
        return stage;
    }
    
    @Override
    public void dispose() {
        stage.dispose();
    }
}
