package de.pcfreak9000.spaceawaits.gui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

import de.pcfreak9000.spaceawaits.core.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;

public class GuiOverlay implements Disposable {
    //Table? Actor? Stage!
    
    protected GameRenderer gameRenderer;
    protected Player player;
    protected Stage stage;
    
    protected boolean reactsToToggleInventory = true;
    
    //Crappy workaround
    private boolean justOpened = true;
    
    public final void create(GameRenderer gameRenderer, Player player) {
        this.gameRenderer = gameRenderer;
        this.player = player;
        this.stage = gameRenderer.getGuiHelper().createStage();
        create();
    }
    
    protected void create() {
        
    }
    
    protected void closeContainer() {
        this.gameRenderer.setGuiCurrent(null);
    }
    
    public void onOpened() {
        
    }
    
    public void onClosed() {
        
    }
    
    public void actAndDraw(float dt) {
        this.gameRenderer.getGuiHelper().drawDarken(0.7f);
        this.gameRenderer.getGuiHelper().actAndDraw(stage, dt);
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
