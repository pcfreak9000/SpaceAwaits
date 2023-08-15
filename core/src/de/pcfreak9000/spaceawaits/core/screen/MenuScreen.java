package de.pcfreak9000.spaceawaits.core.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;

public class MenuScreen extends ScreenAdapter {
    
    protected Stage stage;
    
    private final GuiHelper guiHelper;
    
    public MenuScreen(GuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.stage = this.guiHelper.createStage();
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }
    
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        dispose();
    }
    
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 0);
        this.guiHelper.drawBackground();
        this.guiHelper.actAndDraw(stage, delta);
    }
    
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        this.guiHelper.resize(width, height);
    }
    
    @Override
    public void dispose() {
        this.stage.dispose();
    }
}
