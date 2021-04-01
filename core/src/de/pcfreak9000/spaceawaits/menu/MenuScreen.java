package de.pcfreak9000.spaceawaits.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class MenuScreen extends ScreenAdapter {
    
    protected Stage stage;
    
    private final GuiScreenManager guiScreenManager;
    
    public MenuScreen(GuiScreenManager guipackage) {
        this.guiScreenManager = guipackage;
        this.stage = this.guiScreenManager.createStage();
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
        this.guiScreenManager.actAndDraw(stage, delta);
    }
    
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        this.guiScreenManager.resize(width, height);
    }
    
    @Override
    public void dispose() {
        this.stage.dispose();
    }
}
