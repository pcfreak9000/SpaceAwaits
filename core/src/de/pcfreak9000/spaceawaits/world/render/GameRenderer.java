package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.pcfreak9000.spaceawaits.core.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.menu.GuiHelper;
import de.pcfreak9000.spaceawaits.menu.GuiOverlay;
import de.pcfreak9000.spaceawaits.menu.ScreenManager;

public class GameRenderer extends ScreenAdapter {
    
    private ScreenManager gsm;
    
    private GuiHelper guiHelper;
    
    private FPSLogger fps;
    
    private SpriteBatch spriteBatch;
    private GuiOverlay guiContainerCurrent;
    private Vector2 mousePosVec = new Vector2();
    
    private WorldView worldView;
    
    private View viewCurrent;
    
    private boolean showDebugScreen;
    private DebugScreen debugScreen;
    
    public GameRenderer(ScreenManager gsm, GuiHelper guiHelper) {
        this.gsm = gsm;
        this.guiHelper = guiHelper;
        this.fps = new FPSLogger();
        this.spriteBatch = new SpriteBatch(8191);//8191 is the max sadly...
        this.worldView = new WorldView(guiHelper);
        this.debugScreen = new DebugScreen(this);
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
    
    public WorldView setWorldView() {//Hmm
        this.viewCurrent = worldView;
        return worldView;
    }
    
    public View getView() {
        return viewCurrent;
    }
    
    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }
    
    public Vector2 getMouseWorldPos() {
        return mousePosVec;
    }
    
    public GuiHelper getGuiHelper() {
        return guiHelper;
    }
    
    public void setAdditiveBlending() {
        spriteBatch.enableBlending();
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
    }
    
    public void setDefaultBlending() {
        spriteBatch.enableBlending();
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
    
    public void setMultiplicativeBlending() {
        spriteBatch.enableBlending();
        spriteBatch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO);
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
        ScreenUtils.clear(0.05f, 0.05f, 0.05f, 1);
        applyViewport();
        updateMouseWorldPosCache();
        SpaceAwaits.BUS.post(new RendererEvents.UpdateAnimationEvent(delta));
        viewCurrent.updateAndRenderContent(delta);
        if (showDebugScreen) {
            this.debugScreen.actAndDraw(delta);
        }
        if (this.guiContainerCurrent != null) {
            this.guiContainerCurrent.actAndDraw(delta);
        }
        //fps.log();
        if (exit) {
            SpaceAwaits.getSpaceAwaits().getGameManager().unloadGame();//oof still...
            gsm.setMainMenuScreen();
        }
        InptMgr.clear();
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
    
}
