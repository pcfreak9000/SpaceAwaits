package de.pcfreak9000.spaceawaits.core.screen;

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
import de.pcfreak9000.spaceawaits.core.SpriteBatchImpr;
import de.pcfreak9000.spaceawaits.gui.GuiOverlay;
import de.pcfreak9000.spaceawaits.util.FrameBufferStack;
import de.pcfreak9000.spaceawaits.world.render.RendererEvents;

//TODO Maybe have a GameScreen2D in the hierachy as well?
public abstract class GameScreen extends ScreenAdapter {
    
    /* Technical stuff */
    
    //technical stuff, for guis
    private GuiHelper guiHelper;
    
    private SpriteBatchImpr spriteBatch;
    private Vector2 mousePosVec = new Vector2();
    private FrameBufferStack fbostack;
    private float renderTime = 0;
    
    /* Game related utilities */
    
    private boolean saveAndExitToMainMenu = false;
    private boolean showGui = true;
    
    public GameScreen(GuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.spriteBatch = new SpriteBatchImpr(8191);//8191 is the max sadly...
        this.fbostack = FrameBufferStack.GLOBAL;
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
    
    public boolean isShowGui() {
        return showGui;
    }
    
    public void setShowGui(boolean b) {
        this.showGui = b;
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
        this.spriteBatch.setCamera(vp.getCamera());
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
        //TODO setGuiCurrent(null);
        this.dispose();
    }
    
    @Override
    public void render(float delta) {
        renderTime += delta;
        
        ScreenUtils.clear(0, 0, 0, 1);
        applyViewport();
        updateMouseWorldPosCache();
        //oh boi, this is updating all loaded animations, not just the ones on the screen, not really efficient
        SpaceAwaits.BUS.post(new RendererEvents.UpdateAnimationEvent(delta));
        updateAndRenderContent(delta, showGui);
        
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
        this.guiHelper.resize(width, height);
        SpaceAwaits.BUS.post(new RendererEvents.ResizeWorldRendererEvent(this, width, height));
    }
    
    @Override
    public void dispose() {
        super.dispose();
        this.spriteBatch.dispose();
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
    
    @Deprecated
    public abstract boolean isGuiContainerOpen();
    
    @Deprecated
    public abstract void setGuiCurrent(GuiOverlay guiOverlay);
}
