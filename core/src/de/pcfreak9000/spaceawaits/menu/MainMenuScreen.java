package de.pcfreak9000.spaceawaits.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;

public class MainMenuScreen extends ScreenAdapter {
    
    private ExtendViewport viewport;
    
    private Stage stage;
    private Skin skin;
    
    public MainMenuScreen() {
        this.skin = new Skin(Gdx.files.internal("ui/skin.json"));//TODO license stuff, resource loading stuff
        viewport = new ExtendViewport(200, 200);
        this.stage = new Stage(viewport);
        TextButton playButton = new TextButton("Play", skin);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SpaceAwaits.getSpaceAwaits().setScreen(SpaceAwaits.getSpaceAwaits().worldScreen);
            }
        });
        TextButton exitButton = new TextButton("Quit", skin);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        Table table = new Table();
        table.setWidth(this.stage.getWidth());
        table.setHeight(this.stage.getHeight());
        table.align(Align.center);
        table.add(playButton);
        table.row();
        table.add(exitButton);
        stage.addActor(table);
    }
    
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
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
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void dispose() {
        this.stage.dispose();
        this.skin.dispose();
    }
}
