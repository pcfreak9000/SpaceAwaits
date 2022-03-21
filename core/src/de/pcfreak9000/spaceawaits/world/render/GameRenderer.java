package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.pcfreak9000.spaceawaits.core.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.menu.GuiChat;
import de.pcfreak9000.spaceawaits.menu.GuiHelper;
import de.pcfreak9000.spaceawaits.menu.GuiOverlay;
import de.pcfreak9000.spaceawaits.menu.ScreenManager;
import de.pcfreak9000.spaceawaits.util.FrameBufferStack;

public class GameRenderer extends ScreenAdapter {
    
    private ScreenManager gsm;
    
    private GuiHelper guiHelper;
    
    private SpriteBatchImpr spriteBatch;
    private GuiOverlay guiContainerCurrent;
    private Vector2 mousePosVec = new Vector2();
    private FrameBufferStack fbostack;
    
    private WorldView worldView;
    
    private View viewCurrent;
    
    private boolean showGui = true;
    
    private boolean showDebugScreen;
    private DebugScreen debugScreen;
    
    public GameRenderer(ScreenManager gsm, GuiHelper guiHelper) {
        this.gsm = gsm;
        this.guiHelper = guiHelper;
        this.spriteBatch = new SpriteBatchImpr(8191);//8191 is the max sadly...
        this.worldView = new WorldView(guiHelper);
        this.debugScreen = new DebugScreen(this);
        this.fbostack = new FrameBufferStack();
        setWorldView();
    }
    
    //Always takes a new GuiContainer. Is that the way to go?
    public void setGuiCurrent(GuiOverlay guicont) {
        if (guicont == null && isGuiContainerOpen()) {
            //Possibly closing logic first
            this.guiContainerCurrent.onClosed();
            this.guiContainerCurrent.dispose();
            InptMgr.multiplex(null);
            this.guiContainerCurrent = null;
        } else if (!isGuiContainerOpen()) {
            this.guiContainerCurrent = guicont;
            InptMgr.multiplex(guicont.getStage());
            this.guiContainerCurrent.onOpened();
            //Possibly opening logic
        }
    }
    
    public boolean isGuiContainerOpen() {
        return this.guiContainerCurrent != null;
    }
    
    public void setWorldView() {
        this.viewCurrent = worldView;
    }
    
    public WorldView getWorldView() {
        return worldView;
    }
    
    public View getCurrentView() {
        return viewCurrent;
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
    
    public void setDefaultBlending() {//Doesn't work for the batch
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE,
                GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
    
    public void applyViewport() {
        Viewport vp = viewCurrent.getViewport();
        vp.apply();
        this.spriteBatch.setProjectionMatrix(vp.getCamera().combined);
    }
    
    @Override
    public void show() {
        InptMgr.init();
        super.show();
        worldView.setPlayer(SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().getPlayer());//Thats ugly, also the worldView should be configured elsewhere
    }
    
    @Override
    public void render(float delta) {
        boolean exit = this.guiContainerCurrent == null && InptMgr.isJustPressed(EnumInputIds.Esc);
        if (InptMgr.isJustPressed(EnumInputIds.DebugScreenButton)) {
            showDebugScreen = !showDebugScreen;
        }
        if (InptMgr.isJustPressed(EnumInputIds.HideHud)) {
            showGui = !showGui;
        }
        ScreenUtils.clear(0, 0, 0, 1);
        applyViewport();
        updateMouseWorldPosCache();
        SpaceAwaits.BUS.post(new RendererEvents.UpdateAnimationEvent(delta));
        viewCurrent.updateAndRenderContent(delta, showGui);
        if (showGui) {
            if (showDebugScreen) {
                this.debugScreen.actAndDraw(delta);
            }
            if (InptMgr.isJustPressed(EnumInputIds.Console)) {
                this.setGuiCurrent(new GuiChat(this));
            }
            if (this.guiContainerCurrent != null) {
                this.guiContainerCurrent.actAndDraw(delta);
            }
        }
        //fps.log();
        if (exit) {
            SpaceAwaits.getSpaceAwaits().getGameManager().unloadGame();//oof still...
            gsm.setMainMenuScreen();
        }
        //InptMgr.clear();
    }
    
    private void updateMouseWorldPosCache() {
        mousePosVec.set(Gdx.input.getX(), Gdx.input.getY());
        mousePosVec = this.viewCurrent.getViewport().unproject(mousePosVec);
    }
    
    @Override
    public void resize(int width, int height) {
        this.viewCurrent.getViewport().update(width, height);
        this.guiHelper.resize(width, height);
        SpaceAwaits.BUS.post(new RendererEvents.ResizeWorldRendererEvent(this, width, height));
    }
    
    @Override
    public void dispose() {
        super.dispose();
        this.guiHelper.dispose();
        this.spriteBatch.dispose();
    }
    
    public boolean showGui() {
        return showGui;
    }
    
    public FrameBufferStack getFBOStack() {
        return this.fbostack;
    }
    
}
