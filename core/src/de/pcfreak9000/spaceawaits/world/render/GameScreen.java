package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyphercove.flexbatch.FlexBatch;

import de.pcfreak9000.spaceawaits.command.ICommandContext;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.gui.GuiEsc;
import de.pcfreak9000.spaceawaits.gui.GuiOverlay;
import de.pcfreak9000.spaceawaits.screen.GuiHelper;
import de.pcfreak9000.spaceawaits.util.FrameBufferStack;

//TODO Maybe have a GameScreen2D in the hierachy as well?
public abstract class GameScreen extends ScreenAdapter {
    
    private GuiHelper guiHelper;
    
    private SpriteBatchImpr spriteBatch;
    private Vector2 mousePosVec = new Vector2();
    private FrameBufferStack fbostack;
    
    private boolean showGui = true;
    private GuiOverlay guiContainerCurrent;
    
    private boolean showDebugScreen;
    private DebugScreen debugScreen;
    
    private float renderTime = 0;
    
    private boolean saveAndExitToMainMenu = false;
    
    public GameScreen(GuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.spriteBatch = new SpriteBatchImpr(8191);//8191 is the max sadly...
        this.debugScreen = new DebugScreen(this);
        this.fbostack = new FrameBufferStack();
    }
    
    //Always takes a new GuiContainer. Is that the way to go?
    public void setGuiCurrent(GuiOverlay guicont) {
        if (guicont == null && isGuiContainerOpen()) {
            SpaceAwaits.BUS.post(new RendererEvents.CloseGuiOverlay(this.guiContainerCurrent));
            //Possibly closing logic first
            this.guiContainerCurrent.onClosed();
            this.guiContainerCurrent.dispose();
            InptMgr.multiplex(null);
            this.guiContainerCurrent = null;
        } else if (guicont != null) {// if (!isGuiContainerOpen())
            if (isGuiContainerOpen()) {
                setGuiCurrent(null);
            }
            this.guiContainerCurrent = guicont;
            InptMgr.multiplex(guicont.getStage());
            this.guiContainerCurrent.onOpened();
            SpaceAwaits.BUS.post(new RendererEvents.OpenGuiOverlay(guicont));
            //Possibly opening logic
        }
    }
    
    public boolean isGuiContainerOpen() {
        return this.guiContainerCurrent != null;
    }
    
    public SpriteBatchImpr getSpriteBatch() {
        return spriteBatch;
    }
    
    public Vector2 getMouseWorldPos() {
        return mousePosVec;
    }
    
    public GuiHelper getGuiHelper() {
        return guiHelper;
    }
    
    //Doesn't work for the batch
    public void setDefaultBlending() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE,
                GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
    
    public void setDefaultBlending(FlexBatch<?> batch) {
        batch.enableBlending();
        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE,
                GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
    
    public void setDefaultBlending(SpriteBatch batch) {
        batch.enableBlending();
        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE,
                GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
    
    public void applyViewport() {
        Viewport vp = getViewport();
        vp.apply();
        this.spriteBatch.setProjectionMatrix(vp.getCamera().combined);
    }
    
    @Override
    public void show() {
        InptMgr.init();
        super.show();
    }
    
    @Override
    public void hide() {
        super.hide();
        setGuiCurrent(null);
        this.dispose();
    }
    
    @Override
    public void render(float delta) {
        renderTime += delta;
        if (this.guiContainerCurrent == null && InptMgr.isJustPressed(EnumInputIds.Esc)) {
            GuiEsc gesc = new GuiEsc();
            gesc.createAndOpen(null);//Hmmmmmmm
        }
        if (InptMgr.isJustPressed(EnumInputIds.DebugScreenButton)) {
            showDebugScreen = !showDebugScreen;
        }
        if (InptMgr.isJustPressed(EnumInputIds.HideHud)) {
            showGui = !showGui;
        }
        ScreenUtils.clear(0, 0, 0, 1);
        applyViewport();
        updateMouseWorldPosCache();
        //oh boi, this is updating all loaded animations, not just the ones on the screen, not really efficient
        SpaceAwaits.BUS.post(new RendererEvents.UpdateAnimationEvent(delta));
        updateAndRenderContent(delta, showGui);
        if (showGui) {
            if (showDebugScreen) {
                this.debugScreen.actAndDraw(delta);
            }
            if (this.guiContainerCurrent != null) {
                this.guiContainerCurrent.actAndDraw(delta);
            }
        }
        if (saveAndExitToMainMenu) {
            SpaceAwaits.getSpaceAwaits().getGameManager().unloadGame();
        }
    }
    
    public void queueSaveAndExitToMainMenu() {
        saveAndExitToMainMenu = true;
    }
    
    private void updateMouseWorldPosCache() {
        mousePosVec.set(Gdx.input.getX(), Gdx.input.getY());
        mousePosVec = this.getViewport().unproject(mousePosVec);
    }
    
    @Override
    public void resize(int width, int height) {
        this.getViewport().update(width, height);
        this.guiHelper.resize(width, height);
        SpaceAwaits.BUS.post(new RendererEvents.ResizeWorldRendererEvent(this, width, height));
    }
    
    @Override
    public void dispose() {
        super.dispose();
        this.spriteBatch.dispose();
    }
    
    public boolean showGui() {
        return showGui;
    }
    
    public FrameBufferStack getFBOStack() {
        return this.fbostack;
    }
    
    public float getRenderTime() {
        return renderTime;
    }
    
    public abstract Viewport getViewport();
    
    public abstract Camera getCamera();
    
    public abstract ICommandContext getCommandContext();
    
    public abstract void updateAndRenderContent(float delta, boolean gui);
}
