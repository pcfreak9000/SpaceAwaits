package de.pcfreak9000.spaceawaits.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;

public class MenuScreen extends ScreenAdapter {
    
    protected Stage stage;
    
    private final ScreenManager screenManager;
    
    public MenuScreen(ScreenManager guipackage) {
        this.screenManager = guipackage;
        this.stage = this.screenManager.createStage();
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }
    
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
    
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 0);
        this.screenManager.drawBackground();
        this.screenManager.actAndDraw(stage, delta);
    }
    
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        this.screenManager.resize(width, height);
    }
    
    @Override
    public void dispose() {
        this.stage.dispose();
    }
}
