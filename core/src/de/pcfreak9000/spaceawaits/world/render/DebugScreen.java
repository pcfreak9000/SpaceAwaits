package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.spaceawaits.core.CoreRes;

public class DebugScreen {
    
    private GameRenderer renderer;
    private Stage stage;
    
    private Table table;
    private Label labelFps;
    
    public DebugScreen(GameRenderer renderer) {
        this.renderer = renderer;
        this.stage = renderer.getGuiHelper().createStage();
        this.table = new Table(CoreRes.SKIN.getSkin());
        this.table.setFillParent(true);
        this.table.align(Align.topLeft);
        this.labelFps = new Label("FPS: 0", CoreRes.SKIN.getSkin());
        this.table.add(this.labelFps);
        this.stage.addActor(table);
    }
    
    public void actAndDraw(float dt) {
        this.labelFps.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
        renderer.getGuiHelper().actAndDraw(stage, dt);
    }
}
