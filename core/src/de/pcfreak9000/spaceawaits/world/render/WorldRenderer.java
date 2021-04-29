package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.pcfreak9000.spaceawaits.core.CoreResources.EnumDefInputIds;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.menu.GuiContainer;
import de.pcfreak9000.spaceawaits.menu.ScreenManager;
import de.pcfreak9000.spaceawaits.world2.World;

public class WorldRenderer extends ScreenAdapter {
    
    private ScreenManager gsm;
    
    private World world;
    private FPSLogger fps;
    
    private OrthographicCamera camera;
    private FitViewport viewport;
    
    private SpriteBatch spriteBatch;
    
    private GuiContainer guiContainerCurrent;
    private Vector2 mousePosVec = new Vector2();
    //private int mousePosTileX, mousePosTileY;
    
    public WorldRenderer(ScreenManager gsm) {
        this.gsm = gsm;
        this.fps = new FPSLogger();
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(1920 / 24, 1080 / 24, camera);
        this.spriteBatch = new SpriteBatch(8191);//8191 is the max sadly...
    }
    
    public void setWorld(World world) {
        this.world = world;
    }
    
    public void setGuiCurrent(GuiContainer guicont) {//?????????
        if (guicont == null && this.guiContainerCurrent != null) {
            //Possibly closing logic first
            this.guiContainerCurrent = null;
            InptMgr.setLocked(false, null);
        } else if (this.guiContainerCurrent == null) {
            this.guiContainerCurrent = guicont;
            //Possibly opening logic
            InptMgr.setLocked(true, null);
        }
    }
    
    public OrthographicCamera getCamera() {
        return camera;
    }
    
    public Viewport getViewport() {
        return viewport;
    }
    
    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }
    
    public Vector2 getMouseWorldPos() {
        return mousePosVec;
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
        viewport.apply();
        this.spriteBatch.setProjectionMatrix(camera.combined);
    }
    
    @Override
    public void show() {
        InptMgr.init();
        super.show();
        this.gsm.getHud().setPlayer(SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().getPlayer());//Thats ugly
    }
    
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.05f, 0.05f, 0.05f, 1);
        applyViewport();
        updateMouseWorldPosCache();
        SpaceAwaits.BUS.post(new RendererEvents.UpdateAnimationEvent(delta));
        this.world.update(delta);
        this.gsm.getHud().actAndDraw(delta);
        fps.log();
        if (InptMgr.isJustPressed(EnumDefInputIds.Esc)) {
            SpaceAwaits.getSpaceAwaits().getGameManager().unloadGame();//oof still...
            gsm.setMainMenuScreen();
        }
        InptMgr.clear();
    }
    
    private void updateMouseWorldPosCache() {
        mousePosVec.set(Gdx.input.getX(), Gdx.input.getY());
        mousePosVec = this.viewport.unproject(mousePosVec);
    }
    
    @Override
    public void resize(int width, int height) {
        this.viewport.update(width, height);
        this.gsm.resize(width, height);
        SpaceAwaits.BUS.post(new RendererEvents.ResizeWorldRendererEvent(this, width, height));
    }
    
    @Override
    public void dispose() {
        super.dispose();
        this.spriteBatch.dispose();
    }
}
