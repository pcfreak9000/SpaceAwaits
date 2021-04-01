package de.pcfreak9000.spaceawaits.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
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

public class GuiScreenManager implements Disposable {
    
    private ExtendViewport viewport;
    private FillViewport backgroundVp;
    
    private SpriteBatch guiSpriteBatch;
    private Skin skin;
    
    private ScreenStateManager ssmgr;
    
    private MainMenuScreen mainMenu;
    private SelectSaveScreen selectSaveScreen;
    
    public GuiScreenManager(ScreenStateManager ssmgr) {
        SpaceAwaits.BUS.register(this);
        this.ssmgr = ssmgr;
        this.viewport = new ExtendViewport(500, 500);
        this.backgroundVp = new FillViewport(200 * 16 / 9f, 200);
        this.guiSpriteBatch = new SpriteBatch();
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
        mainMenu = new MainMenuScreen(ssmgr, this);
        if (selectSaveScreen != null) {
            selectSaveScreen.dispose();
        }
        selectSaveScreen = new SelectSaveScreen(ssmgr, this);
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
        return new Stage(viewport, guiSpriteBatch);
    }
    
    public void actAndDraw(Stage stage, float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        backgroundVp.apply(true);
        guiSpriteBatch.setProjectionMatrix(backgroundVp.getCamera().combined);
        guiSpriteBatch.begin();
        float w = backgroundVp.getWorldWidth();
        float h = backgroundVp.getWorldHeight();
        guiSpriteBatch.draw(CoreResources.SPACE_BACKGROUND.getRegion(), 0, 0, w, h);
        guiSpriteBatch.end();
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
