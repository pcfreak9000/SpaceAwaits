package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.core.screen.TileScreen;
import de.pcfreak9000.spaceawaits.world.chunk.Chunk;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkSystem;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.render.ecs.CameraSystem;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class DebugOverlay {

    private GameScreen renderer;
    private Stage stage;

    private Table table;
    private Label labelFps;
    private Label playerPos;
    private Label chunkPos;
    private Label chunkUpdates;
    private Label tile;
    private Label meta;
    private Label seed;

    private Label gamemode;

    private Label time;

    public DebugOverlay(GameScreen renderer) {
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
        this.seed = new Label("", CoreRes.SKIN.getSkin());
        this.table.add(this.seed).align(Align.left);
        this.table.row();
        this.time = new Label("", CoreRes.SKIN.getSkin());
        this.table.add(this.time).align(Align.left);
        this.table.row();
        this.gamemode = new Label("", CoreRes.SKIN.getSkin());
        this.table.add(this.gamemode).align(Align.left);
        this.stage.addActor(table);
    }

    public void actAndDraw(float dt) {
        int fps = Gdx.graphics.getFramesPerSecond();
        Vector2 playerPos = Components.TRANSFORM.get(
                SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().getPlayer().getPlayerEntity()).position;
        int cx = Chunk.toGlobalChunkf(playerPos.x);
        int cy = Chunk.toGlobalChunkf(playerPos.y);
        GameScreen gs = SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().getGameScreenCurrent();
        if (gs instanceof TileScreen) {
            TileScreen world = (TileScreen) gs;
            int loadedChunks = world.getSystem(ChunkSystem.class).getLoadedChunksCount();// TODO gamescreen ecs stuff
            int updatedChunks = world.getSystem(ChunkSystem.class).getUpdatingChunksCount();
            this.chunkUpdates.setText(String.format("up: %d ld: %d", updatedChunks, loadedChunks));
            int tx = Tile.toGlobalTile(world.getSystem(CameraSystem.class).getMouseWorldPos().x);
            int ty = Tile.toGlobalTile(world.getSystem(CameraSystem.class).getMouseWorldPos().y);
            TileSystem ts = world.getSystem(TileSystem.class);
            Tile front = ts.getTile(tx, ty, TileLayer.Front);
            Tile back = ts.getTile(tx, ty, TileLayer.Back);
            this.tile.setText("Looking at tx: " + tx + " ty: " + ty + " f: " + getDisplayName(front) + " b: "
                    + getDisplayName(back));// Hmmm
            this.chunkPos.setText(String.format("cx: %d cy: %d", cx, cy));
        }
        this.labelFps.setText("FPS: " + fps);
        this.playerPos.setText(String.format("x: %.3f y: %.3f", playerPos.x, playerPos.y));
        this.meta.setText("Not displaying meta");
        this.seed.setText(
                "Master Seed: " + SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().getMasterSeed());
        this.time.setText(String.format("time: %d", 0));
        renderer.getGuiHelper().actAndDraw(stage, dt);
        this.gamemode.setText("Gamemode: "
                + SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().getPlayer().getGameMode().toString());
    }

    private String getDisplayName(Tile t) {
        return t == null ? "null" : t.getDisplayName();
    }

}
