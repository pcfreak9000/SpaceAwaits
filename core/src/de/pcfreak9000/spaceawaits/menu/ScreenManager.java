package de.pcfreak9000.spaceawaits.menu;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.CoreEvents.QueueResourcesEvent;
import de.pcfreak9000.spaceawaits.core.CoreEvents.UpdateResourcesEvent;
import de.pcfreak9000.spaceawaits.core.CoreResources;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.render.Hud;
import de.pcfreak9000.spaceawaits.world.render.WorldRenderer;

public class ScreenManager implements Disposable {
    
    private SpaceAwaits space;
    
    private ExtendViewport viewport;
    private FillViewport backgroundVp;
    
    private SpriteBatch guiSpriteBatch;
    private Skin skin;
    
    private MainMenuScreen mainMenu;
    private SelectSaveScreen selectSaveScreen;
    private Hud hud;
    private WorldRenderer worldScreen;
    
    public ScreenManager(SpaceAwaits space) {
        this.space = space;
        SpaceAwaits.BUS.register(this);
        this.viewport = new ExtendViewport(500, 500);
        this.backgroundVp = new FillViewport(200 * 16 / 9f, 200);
        this.guiSpriteBatch = new SpriteBatch();
        this.worldScreen = new WorldRenderer(this);
    }
    
    @EventSubscription
    private void resEv1(QueueResourcesEvent ev) {
        ev.assetMgr.load("ui/skin.json", Skin.class);//TODO license stuff
    }
    
    @EventSubscription
    private void resEv2(UpdateResourcesEvent ev) {
        this.skin = ev.assetMgr.get("ui/skin.json", Skin.class);
        reload();
    }
    
    private void reload() {
        if (mainMenu != null) {
            mainMenu.dispose();
        }
        mainMenu = new MainMenuScreen(this);
        if (selectSaveScreen != null) {
            selectSaveScreen.dispose();
        }
        selectSaveScreen = new SelectSaveScreen(this);
        if (hud != null) {
            hud.dispose();
        }
        hud = new Hud(this);
    }
    
    public void setMainMenuScreen() {
        this.space.setScreen(this.mainMenu);
    }
    
    public void setSelectSaveScreen() {
        this.space.setScreen(this.selectSaveScreen);
    }
    
    public void setWorldScreen() {
        this.space.setScreen(worldScreen);
    }
    
    public WorldRenderer getWorldRenderer() {
        return this.worldScreen;
    }
    
    public Hud getHud() {
        return hud;
    }
    
    public MainMenuScreen getMainMenu() {
        return this.mainMenu;
    }
    
    public SelectSaveScreen getSelectSave() {
        return this.selectSaveScreen;
    }
    
    public Skin getSkin() {
        return skin;
    }
    
    public Stage createStage() {
        Stage st = new Stage(viewport, guiSpriteBatch);
        //st.setDebugAll(true);
        return st;
    }
    
    public void drawBackground() {
        backgroundVp.apply(true);
        guiSpriteBatch.setProjectionMatrix(backgroundVp.getCamera().combined);
        guiSpriteBatch.begin();
        float w = backgroundVp.getWorldWidth();
        float h = backgroundVp.getWorldHeight();
        guiSpriteBatch.draw(CoreResources.SPACE_BACKGROUND.getRegion(), 0, 0, w, h);
        guiSpriteBatch.end();
    }
    
    public void actAndDraw(Stage stage, float delta) {
        viewport.apply();
        stage.act(delta);
        stage.draw();
    }
    
    public void showDialog(String title, String text, Stage stage) {
        Dialog d = new Dialog(title, getSkin());
        d.text(text);
        d.button("Ok");
        d.show(stage);
    }
    
    public void resize(int width, int height) {
        viewport.update(width, height);
        backgroundVp.update(width, height);
    }
    
    @Override
    public void dispose() {
        this.guiSpriteBatch.dispose();
        this.skin.dispose();
        this.selectSaveScreen.dispose();
        this.mainMenu.dispose();
    }
}
