package de.pcfreak9000.spaceawaits.core.screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;

import de.pcfreak9000.spaceawaits.core.assets.CoreRes;

public class GuiHelper implements Disposable {
    
    private ExtendViewport viewport;
    private FillViewport backgroundVp;
    
    private SpriteBatch guiSpriteBatch;
    
    public GuiHelper() {
        this.viewport = new ExtendViewport(1280 / 1.9f, 720 / 1.9f, 1920 / 1.9f, 1920 / 1.9f);
        this.backgroundVp = new FillViewport(200 * 16 / 9f, 200);
        this.guiSpriteBatch = new SpriteBatch();
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
        CoreRes.SPACE_BACKGROUND_2.render(guiSpriteBatch, 0, 0, w, h);
        //guiSpriteBatch.draw(CoreRes.SPACE_BACKGROUND_2.getRegion(), 0, 0, w, h);
        guiSpriteBatch.end();
    }
    
    public void drawDarken(float alpha) {
        viewport.apply(true);
        guiSpriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        guiSpriteBatch.begin();
        guiSpriteBatch.setColor(0.1f, 0.1f, 0.1f, alpha);
        guiSpriteBatch.draw(CoreRes.WHITE, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        guiSpriteBatch.end();
    }
    
    public void actAndDraw(Stage stage, float delta) {
        viewport.apply(true);
        stage.act(delta);
        stage.draw();
    }
    
    public void showDialog(String title, String text, Stage stage) {
        if (title.length() > 40) {
            title = title.substring(0, 40) + "...";
        }
        if (text.length() > 55) {
            text = text.substring(0, 55) + "...";
        }
        Dialog d = new Dialog(title, CoreRes.SKIN.getSkin());
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
    }
}
