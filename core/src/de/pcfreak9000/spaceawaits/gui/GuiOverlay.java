package de.pcfreak9000.spaceawaits.gui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

import de.pcfreak9000.spaceawaits.core.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.render.GameScreen;

public class GuiOverlay implements Disposable {
    //Table? Actor? Stage!
    
    protected GameScreen gameScreen;
    protected Player player;
    protected Stage stage;
    
    protected boolean reactsToToggleInventory = true;
    
    //Crappy workaround
    private boolean justOpened = true;
    
    public final void create(GameScreen gameScreen, Player player) {
        this.gameScreen = gameScreen;
        this.player = player;
        this.stage = gameScreen.getGuiHelper().createStage();
        create();
    }
    
    protected void create() {
        
    }
    
    protected void closeContainer() {
        this.gameScreen.setGuiCurrent(null);
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
