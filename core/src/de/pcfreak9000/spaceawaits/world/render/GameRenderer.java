package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.pcfreak9000.spaceawaits.core.CoreRes.EnumDefInputIds;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.menu.GuiContainer;
import de.pcfreak9000.spaceawaits.menu.GuiHelper;
import de.pcfreak9000.spaceawaits.menu.ScreenManager;

public class GameRenderer extends ScreenAdapter {
    
    private ScreenManager gsm;
    
    private GuiHelper guiHelper;
    
    private FPSLogger fps;
    
    private SpriteBatch spriteBatch;
    private GuiContainer guiContainerCurrent;
    private Vector2 mousePosVec = new Vector2();
    
    private WorldView worldView;
    
    private View viewCurrent;
    
    public GameRenderer(ScreenManager gsm, GuiHelper guiHelper) {
        this.gsm = gsm;
        this.guiHelper = guiHelper;
        this.fps = new FPSLogger();
        this.spriteBatch = new SpriteBatch(8191);//8191 is the max sadly...
        this.worldView = new WorldView(guiHelper);
        setWorldView();
    }
    
    //Always takes a new GuiContainer. Is that the way to go?
    public void setGuiCurrent(GuiContainer guicont) {
        if (guicont == null && isGuiContainerOpen()) {
            //Possibly closing logic first
            this.guiContainerCurrent.dispose();
            this.guiContainerCurrent = null;
        } else if (!isGuiContainerOpen()) {
            this.guiContainerCurrent = guicont;
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
        ScreenUtils.clear(0.05f, 0.05f, 0.05f, 1);
        applyViewport();
        updateMouseWorldPosCache();
        SpaceAwaits.BUS.post(new RendererEvents.UpdateAnimationEvent(delta));
        viewCurrent.updateAndRenderContent(delta);
        if (this.guiContainerCurrent != null) {
            guiHelper.actAndDraw(this.guiContainerCurrent.getStage(), delta);
        }
        fps.log();
        if (this.guiContainerCurrent == null && InptMgr.isJustPressed(EnumDefInputIds.Esc)) {
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
