package de.pcfreak9000.spaceawaits.menu;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

import de.pcfreak9000.spaceawaits.core.CoreRes.EnumDefInputIds;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;

public class GuiOverlay implements Disposable {
    //Table? Actor? Stage!
    
    protected final GameRenderer gameRenderer;
    protected final Stage stage;
    
    //Crappy workaround
    private boolean justOpened = true;
    
    public GuiOverlay(GameRenderer gameRenderer) {
        this.gameRenderer = gameRenderer;
        this.stage = gameRenderer.getGuiHelper().createStage();
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
        if (!justOpened && (InptMgr.isJustPressed(EnumDefInputIds.Esc)
                || InptMgr.isJustPressed(EnumDefInputIds.ToggleInventory))) {
            closeContainer();
        }
        if (justOpened) {
            justOpened = false;
        }
    }
    
    public Stage getStage() {
        return stage;
    }
    
    @Override
    public void dispose() {
        stage.dispose();
    }
}
