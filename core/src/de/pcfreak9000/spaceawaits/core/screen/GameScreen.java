package de.pcfreak9000.spaceawaits.core.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.ScreenUtils;

import de.pcfreak9000.spaceawaits.command.ICommandContext;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.gui.GuiOverlay;
import de.pcfreak9000.spaceawaits.world.render.RendererEvents;

//TODO Maybe have a GameScreen2D in the hierachy as well?
public abstract class GameScreen extends ScreenAdapter {
    
    /* Technical stuff */
    
    private GuiHelper guiHelper;
    private RenderHelper2D renderHelper2D;
    
    private float renderTime = 0;
    
    /* Game related utilities */
    
    private boolean saveAndExitToMainMenu = false;
    private boolean showGuiElements = true;
    
    public GameScreen(GuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.renderHelper2D = new RenderHelper2D();
    }
    
    public GuiHelper getGuiHelper() {
        return guiHelper;
    }
    
    public RenderHelper2D getRenderHelper() {
        return renderHelper2D;
    }
    
    public boolean isShowGuiElements() {
        return showGuiElements;
    }
    
    public void setShowGuiElements(boolean b) {
        this.showGuiElements = b;
    }
    
    @Override
    public void show() {
        InptMgr.init();
        super.show();
    }
    
    @Override
    public void hide() {
        super.hide();
        this.dispose();
    }
    
    @Override
    public void render(float delta) {
        renderTime += delta;
        
        ScreenUtils.clear(0, 0, 0, 1);
        SpaceAwaits.BUS.post(new RendererEvents.PreFrameEvent());
        //oh boi, this is updating all loaded animations, not just the ones on the screen, not really efficient
        SpaceAwaits.BUS.post(new RendererEvents.UpdateAnimationEvent(delta));
        updateAndRenderContent(delta, showGuiElements);
        
        if (saveAndExitToMainMenu) {
            SpaceAwaits.getSpaceAwaits().getGameManager().unloadGame();
        }
    }
    
    public void queueSaveAndExitToMainMenu() {
        saveAndExitToMainMenu = true;
    }
    
    @Override
    public void resize(int width, int height) {
        this.guiHelper.resize(width, height);
        SpaceAwaits.BUS.post(new RendererEvents.ResizeWorldRendererEvent(this, width, height));
    }
    
    @Override
    public void dispose() {
        super.dispose();
        this.renderHelper2D.dispose();
    }
    
    public float getRenderTime() {
        return renderTime;
    }
    
    public abstract ICommandContext getCommandContext();
    
    public abstract void updateAndRenderContent(float delta, boolean gui);
    
    //Removing this needs some more refactoring
    @Deprecated
    public abstract void setGuiCurrent(GuiOverlay guiOverlay);
}
