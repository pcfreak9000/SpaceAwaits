package de.pcfreak9000.spaceawaits.core.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.ScreenUtils;

import de.pcfreak9000.spaceawaits.command.ICommandContext;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.core.ecs.EngineImproved;
import de.pcfreak9000.spaceawaits.core.ecs.content.GuiOverlaySystem;
import de.pcfreak9000.spaceawaits.gui.GuiOverlay;
import de.pcfreak9000.spaceawaits.world.command.WorldCommandContext;
import de.pcfreak9000.spaceawaits.world.render.RendererEvents;

//TODO Maybe have a GameScreen2D in the hierachy as well?
public class GameScreen extends ScreenAdapter {
    public static final float STEPLENGTH_SECONDS = 1 / 60f;
    
    /* Technical stuff */
    
    private GuiHelper guiHelper;
    private RenderHelper2D renderHelper2D;
    
    private float renderTime = 0;
    
    /* Game related utilities */
    
    private boolean saveAndExitToMainMenu = false;
    private boolean showGuiElements = true;
    
    /* Game related stuff */
    
    protected final EngineImproved ecsEngine;
    
    private WorldCommandContext commands;
    
    public GameScreen(GuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.renderHelper2D = new RenderHelper2D();
        
        this.ecsEngine = new EngineImproved(STEPLENGTH_SECONDS);
        SpaceAwaits.BUS.register(this.ecsEngine.getEventBus());//Not too sure about this
        
        this.commands = new WorldCommandContext();
        
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
        ecsEngine.update(delta);
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
    
    public ICommandContext getCommandContext() {
        return commands;
    }
    
    //Removing this needs some more refactoring
    @Deprecated
    public void setGuiCurrent(GuiOverlay guiOverlay) {
        ecsEngine.getSystem(GuiOverlaySystem.class).setGuiCurrent(guiOverlay);
    }
    
}
