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
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class DebugScreen {
    
    private static final ComponentMapper<TransformComponent> TRANSFORM_MAPPER = ComponentMapper
            .getFor(TransformComponent.class);
    
    private GameRenderer renderer;
    private Stage stage;
    
    private Table table;
    private Label labelFps;
    private Label playerPos;
    private Label chunkPos;
    private Label chunkUpdates;
    private Label tile;
    private Label meta;
    
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
        this.chunkPos = new Label("", CoreRes.SKIN.getSkin());
        this.table.add(this.chunkPos).align(Align.left);
        this.table.row();
        this.tile = new Label("", CoreRes.SKIN.getSkin());
        this.table.add(tile).align(Align.left);
        this.table.row();
        this.meta = new Label("", CoreRes.SKIN.getSkin());
        this.table.add(meta).align(Align.left);
        this.table.row();
        this.time = new Label("", CoreRes.SKIN.getSkin());
        this.table.add(this.time).align(Align.left);
        this.stage.addActor(table);
    }
    
    public void actAndDraw(float dt) {
        int fps = Gdx.graphics.getFramesPerSecond();
        Vector2 playerPos = TRANSFORM_MAPPER.get(
                SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().getPlayer().getPlayerEntity()).position;
        int cx = Chunk.toGlobalChunkf(playerPos.x);
        int cy = Chunk.toGlobalChunkf(playerPos.y);
        World world = SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().getWorldCurrent();
        int loadedChunks = world.getLoadedChunksCount();
        int updatedChunks = world.getUpdatingChunksCount();
        long time = world.time;//oof
        this.labelFps.setText("FPS: " + fps);
        this.chunkUpdates.setText(String.format("t: %d l: %d", updatedChunks, loadedChunks));
        this.playerPos.setText(String.format("x: %.3f y: %.3f", playerPos.x, playerPos.y));
        this.chunkPos.setText(String.format("cx: %d cy: %d", cx, cy));
        int tx = Tile.toGlobalTile(renderer.getMouseWorldPos().x);
        int ty = Tile.toGlobalTile(renderer.getMouseWorldPos().y);
        TileSystem ts = world.getSystem(TileSystem.class);
        Tile front = ts.getTile(tx, ty, TileLayer.Front);
        Tile back = ts.getTile(tx, ty, TileLayer.Back);
        this.tile.setText(
                "Looking at tx: " + tx + " ty: " + ty + " f: " + getDisplayName(front) + " b: " + getDisplayName(back));//Hmmm
        //this.meta.setText(null);
        this.time.setText(String.format("time: %d", time));
        renderer.getGuiHelper().actAndDraw(stage, dt);
    }
    
    private String getDisplayName(Tile t) {
        return t == null ? "null" : t.getDisplayName();
    }
    
}
