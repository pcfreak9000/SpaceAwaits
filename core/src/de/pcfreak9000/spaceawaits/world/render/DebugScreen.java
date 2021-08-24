package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;

public class DebugScreen {
    
    private static final ComponentMapper<TransformComponent> TRANSFORM_MAPPER = ComponentMapper
            .getFor(TransformComponent.class);
    
    private GameRenderer renderer;
    private Stage stage;
    
    private Table table;
    private Label labelFps;
    private Label playerPos;
    private Label chunkUpdates;
    
    private Label time;
    
    public DebugScreen(GameRenderer renderer) {
        this.renderer = renderer;
        this.stage = renderer.getGuiHelper().createStage();
        this.table = new Table(CoreRes.SKIN.getSkin());
        this.table.setFillParent(true);
        this.table.align(Align.topLeft);
        this.labelFps = new Label("", CoreRes.SKIN.getSkin());
        this.table.add(this.labelFps).align(Align.left);
        this.table.row();
        this.chunkUpdates = new Label("", CoreRes.SKIN.getSkin());
        this.table.add(this.chunkUpdates).align(Align.left);
        this.table.row();
        this.playerPos = new Label("", CoreRes.SKIN.getSkin());
        this.table.add(this.playerPos).align(Align.left);
        this.table.row();
        this.time = new Label("", CoreRes.SKIN.getSkin());
        this.table.add(this.time).align(Align.left);
        this.stage.addActor(table);
    }
    
    public void actAndDraw(float dt) {
        int fps = Gdx.graphics.getFramesPerSecond();
        Vector2 playerPos = TRANSFORM_MAPPER.get(
                SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().getPlayer().getPlayerEntity()).position;
        int loadedChunks = SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().getWorldCurrent()
                .getLoadedChunksCount();
        int updatedChunks = SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().getWorldCurrent()
                .getUpdatingChunksCount();
        long time = SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().getWorldCurrent().time;
        this.labelFps.setText("FPS: " + fps);
        this.chunkUpdates.setText(String.format("t: %d l: %d", updatedChunks, loadedChunks));
        this.playerPos.setText(String.format("x: %.3f y: %.3f", playerPos.x, playerPos.y));
        this.time.setText(String.format("time: %d", time));
        renderer.getGuiHelper().actAndDraw(stage, dt);
    }
}
