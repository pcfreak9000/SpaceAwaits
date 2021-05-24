package de.pcfreak9000.spaceawaits.menu;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

public class GuiContainer implements Disposable {
    //Table? Actor? Stage!
    
    protected final GuiHelper guiHelper;
    protected final Stage stage;
    
    public GuiContainer(GuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.stage = guiHelper.createStage();
    }
    
    public Stage getStage() {
        return stage;
    }
    
    @Override
    public void dispose() {
        stage.dispose();
    }
}
