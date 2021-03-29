package de.pcfreak9000.spaceawaits.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;

import de.pcfreak9000.spaceawaits.core.CoreResources;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;

public class MainMenuScreen extends ScreenAdapter {
    
    private ExtendViewport viewport;
    private FillViewport backgroundVp;
    
    private SpriteBatch batch;//TODO global batch? also see ChunkRenderer with SpriteCache
    
    private Stage stage;
    private Skin skin;
    Table table = new Table();
    
    public MainMenuScreen() {
        this.skin = new Skin(Gdx.files.internal("ui/skin.json"));//TODO license stuff, resource loading stuff
        viewport = new ExtendViewport(200, 200);
        this.stage = new Stage(viewport);
        backgroundVp = new FillViewport(200 * 16 / 9f, 200);
        TextButton playButton = new TextButton("Play", skin);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SpaceAwaits.getSpaceAwaits().setScreen(SpaceAwaits.getSpaceAwaits().worldRenderer);
            }
        });
        TextButton exitButton = new TextButton("Quit", skin);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SpaceAwaits.getSpaceAwaits().exit();
            }
        });
        Label label = new Label(SpaceAwaits.NAME, this.skin);
        label.setFontScale(2);
        this.batch = new SpriteBatch();
        table.setWidth(this.stage.getWidth());
        table.setHeight(this.stage.getHeight());
        table.align(Align.center);
        table.add(label);
        table.row();
        table.add(playButton).width(110).padBottom(5);
        table.row();
        table.add(exitButton).width(110);
        stage.addActor(table);
    }
    
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
        backgroundVp.update(width, height);
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
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        backgroundVp.apply(true);
        batch.setProjectionMatrix(backgroundVp.getCamera().combined);
        batch.begin();
        float w = backgroundVp.getWorldWidth();
        float h = backgroundVp.getWorldHeight();
        batch.draw(CoreResources.SPACE_BACKGROUND.getRegion(), 0, 0, w, h);
        batch.end();
        viewport.apply();
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void dispose() {
        this.stage.dispose();
        this.skin.dispose();
        this.batch.dispose();
    }
}
